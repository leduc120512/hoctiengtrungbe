package com.example.bewebtiengtrung.module.ai.service;

import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.module.ai.config.AiProperties;
import com.example.bewebtiengtrung.module.ai.dto.CompletedWord;
import com.example.bewebtiengtrung.module.ai.dto.GeneratedSentence;
import com.example.bewebtiengtrung.module.ai.dto.LearnedWordBrief;
import com.example.bewebtiengtrung.module.ai.dto.SentenceBatch;
import com.example.bewebtiengtrung.module.ai.dto.WordBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Sinh câu và điền từ bằng Google Gemini qua REST API {@code generateContent}.
 *
 * <p>Lý do có provider này: Google AI Studio cấp API key <b>miễn phí</b> (không cần thẻ), phù hợp
 * cho một người dùng tự học. Prompt dùng chung với Claude ({@link ClaudeSentenceGenerator#SYSTEM_PROMPT},
 * {@link ClaudeSentenceGenerator#WORD_SYSTEM_PROMPT} và các hàm dựng prompt người dùng) để chất lượng
 * hai bên nhất quán; kết quả cũng đi qua cùng bộ lọc ở tầng service.
 *
 * <p>Yêu cầu JSON có lược đồ ({@code responseMimeType} + {@code responseSchema}) để không phải bóc
 * tách văn bản tự do. Không bao giờ log API key.
 */
@Service
public class GeminiSentenceGenerator implements AiSentenceGenerator, AiWordCompleter {

    private static final Logger log = LoggerFactory.getLogger(GeminiSentenceGenerator.class);
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

    /** Lược đồ JSON bắt buộc cho câu trả lời sinh câu — khớp {@link SentenceBatch}. */
    private static final Map<String, Object> SENTENCE_SCHEMA = Map.of(
            "type", "OBJECT",
            "properties", Map.of(
                    "sentences", Map.of(
                            "type", "ARRAY",
                            "items", Map.of(
                                    "type", "OBJECT",
                                    "properties", Map.of(
                                            "hanzi", Map.of("type", "STRING"),
                                            "pinyin", Map.of("type", "STRING"),
                                            "vi", Map.of("type", "STRING"),
                                            "level", Map.of("type", "INTEGER")),
                                    "required", List.of("hanzi", "pinyin", "vi", "level")))),
            "required", List.of("sentences"));

    /** Lược đồ JSON bắt buộc cho câu trả lời điền từ — khớp {@link WordBatch}. */
    private static final Map<String, Object> WORD_SCHEMA = Map.of(
            "type", "OBJECT",
            "properties", Map.of(
                    "words", Map.of(
                            "type", "ARRAY",
                            "items", Map.of(
                                    "type", "OBJECT",
                                    "properties", Map.of(
                                            "simplified", Map.of("type", "STRING"),
                                            "pinyin", Map.of("type", "STRING"),
                                            "meaningVi", Map.of("type", "STRING"),
                                            "meaningEn", Map.of("type", "STRING")),
                                    "required", List.of("simplified", "pinyin", "meaningVi", "meaningEn")))),
            "required", List.of("words"));

    private final AiProperties props;
    private final ObjectMapper mapper;
    private volatile RestClient client;

    public GeminiSentenceGenerator(AiProperties props, ObjectMapper mapper) {
        this.props = props;
        this.mapper = mapper;
    }

    @Override
    public boolean isEnabled() {
        return props.hasGeminiKey();
    }

    @Override
    public String model() {
        return props.getGeminiModel();
    }

    @Override
    public List<GeneratedSentence> generate(List<LearnedWordBrief> words, List<String> avoidHanzi,
                                            int count, Integer level, List<String> focusWords) {
        if (!isEnabled()) {
            throw disabled();
        }
        int ask = ClaudeSentenceGenerator.askCount(count);
        String userPrompt = ClaudeSentenceGenerator.buildUserPrompt(words, avoidHanzi, ask, level, focusWords);

        log.info("Gọi Gemini {}: xin {} câu (cần {}), {} từ đã học, {} từ ưu tiên, tránh {} câu đã có",
                props.getGeminiModel(), ask, count, words.size(),
                focusWords == null ? 0 : focusWords.size(), avoidHanzi == null ? 0 : avoidHanzi.size());
        // Nhiệt độ cao hơn mặc định để các câu trong cùng đợt khác nhau về cấu trúc.
        String raw = call(ClaudeSentenceGenerator.SYSTEM_PROMPT, userPrompt, SENTENCE_SCHEMA, 0.9);
        SentenceBatch batch = GeminiResponseParser.parse(raw, mapper);
        log.info("Gemini trả về {} câu thô", batch.sentences().size());
        return batch.sentences();
    }

    @Override
    public List<CompletedWord> completeWords(String text, int hskLevel, List<String> learnedHanzi, int maxWords) {
        if (!isEnabled()) {
            throw disabled();
        }
        String userPrompt = ClaudeSentenceGenerator.buildWordPrompt(text, hskLevel, learnedHanzi, maxWords);
        log.info("Gọi Gemini {} điền từ: {} ký tự, cấp HSK {}, tối đa {} từ",
                props.getGeminiModel(), text == null ? 0 : text.length(), hskLevel, maxWords);
        // Điền từ là việc tra cứu, cần chính xác chứ không cần sáng tạo.
        String raw = call(ClaudeSentenceGenerator.WORD_SYSTEM_PROMPT, userPrompt, WORD_SCHEMA, 0.2);
        WordBatch batch = GeminiResponseParser.parseWords(raw, mapper);
        log.info("Gemini trả về {} từ", batch.words().size());
        return batch.words();
    }

    /** Gọi {@code generateContent} với system prompt, prompt người dùng và lược đồ JSON bắt buộc. */
    private String call(String systemPrompt, String userPrompt, Map<String, Object> schema, double temperature) {
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", schema,
                        "temperature", temperature,
                        "maxOutputTokens", 8192));
        try {
            return client().post()
                    .uri("/models/{model}:generateContent", props.getGeminiModel())
                    .header("x-goog-api-key", props.resolvedGeminiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            throw mapHttpError(e, props.getGeminiModel());
        } catch (ResourceAccessException e) {
            throw new ApiException(HttpStatus.GATEWAY_TIMEOUT, "AI_TIMEOUT",
                    "Không kết nối được tới Gemini hoặc quá thời gian chờ");
        }
    }

    private static ApiException disabled() {
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_DISABLED",
                "Chưa cấu hình GEMINI_API_KEY trên máy chủ");
    }

    /** Ánh xạ mã HTTP của Google thành lỗi có ý nghĩa cho người dùng; không lộ body chứa key. */
    static ApiException mapHttpError(RestClientResponseException e, String model) {
        int status = e.getStatusCode().value();
        if (status == 429) {
            return new ApiException(HttpStatus.TOO_MANY_REQUESTS, "AI_RATE_LIMIT",
                    "AI đang quá tải (hết hạn mức free tier), thử lại sau ít phút");
        }
        // 404 là model sai hoặc Google đã ngừng cấp model đó cho tài khoản mới — key vẫn tốt,
        // chỉ cần đổi GEMINI_MODEL. Không gộp với lỗi key để người dùng khỏi đi tạo key mới vô ích.
        if (status == 404) {
            return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_CONFIG",
                    "Model Gemini \"" + model + "\" không tồn tại hoặc không còn dùng được cho tài khoản này"
                            + " — đổi biến GEMINI_MODEL (vd. gemini-3.6-flash) trên máy chủ (HTTP 404)");
        }
        if (status == 400 || status == 401 || status == 403) {
            return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_CONFIG",
                    "GEMINI_API_KEY không hợp lệ hoặc chưa được bật (HTTP " + status + ")");
        }
        return new ApiException(HttpStatus.BAD_GATEWAY, "AI_UPSTREAM", "Lỗi từ dịch vụ Gemini (HTTP " + status + ")");
    }

    private RestClient client() {
        RestClient c = client;
        if (c == null) {
            synchronized (this) {
                if (client == null) {
                    client = RestClient.builder().baseUrl(BASE_URL).build();
                }
                c = client;
            }
        }
        return c;
    }
}
