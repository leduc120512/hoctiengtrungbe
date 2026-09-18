package com.example.bewebtiengtrung.module.ai.service;

import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.module.ai.config.AiProperties;
import com.example.bewebtiengtrung.module.ai.dto.GeneratedSentence;
import com.example.bewebtiengtrung.module.ai.dto.LearnedWordBrief;
import com.example.bewebtiengtrung.module.ai.dto.SentenceBatch;
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
 * Sinh câu bằng Google Gemini qua REST API {@code generateContent}.
 *
 * <p>Lý do có provider này: Google AI Studio cấp API key <b>miễn phí</b> (không cần thẻ), phù hợp
 * cho một người dùng tự học. Prompt dùng chung với Claude ({@link ClaudeSentenceGenerator#SYSTEM_PROMPT}
 * và {@link ClaudeSentenceGenerator#buildUserPrompt}) để chất lượng hai bên nhất quán; kết quả cũng
 * đi qua cùng bộ lọc "chỉ dùng chữ đã học" ở tầng service.
 *
 * <p>Yêu cầu JSON có lược đồ ({@code responseMimeType} + {@code responseSchema}) để không phải bóc
 * tách văn bản tự do. Không bao giờ log API key.
 */
@Service
public class GeminiSentenceGenerator implements AiSentenceGenerator {

    private static final Logger log = LoggerFactory.getLogger(GeminiSentenceGenerator.class);
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

    /** Lược đồ JSON bắt buộc cho câu trả lời — khớp {@link SentenceBatch}. */
    private static final Map<String, Object> RESPONSE_SCHEMA = Map.of(
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
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_DISABLED",
                    "Chưa cấu hình GEMINI_API_KEY trên máy chủ");
        }
        int ask = count + count / 3;
        String userPrompt = ClaudeSentenceGenerator.buildUserPrompt(words, avoidHanzi, ask, level, focusWords);

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", ClaudeSentenceGenerator.SYSTEM_PROMPT))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", RESPONSE_SCHEMA,
                        "temperature", 0.9,
                        "maxOutputTokens", 8192));

        log.info("Gọi Gemini {}: xin {} câu (cần {}), {} từ đã học, tránh {} câu đã có",
                props.getGeminiModel(), ask, count, words.size(), avoidHanzi == null ? 0 : avoidHanzi.size());
        String raw;
        try {
            raw = client().post()
                    .uri("/models/{model}:generateContent", props.getGeminiModel())
                    .header("x-goog-api-key", props.resolvedGeminiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            throw mapHttpError(e);
        } catch (ResourceAccessException e) {
            throw new ApiException(HttpStatus.GATEWAY_TIMEOUT, "AI_TIMEOUT",
                    "Không kết nối được tới Gemini hoặc quá thời gian chờ");
        }
        SentenceBatch batch = GeminiResponseParser.parse(raw, mapper);
        log.info("Gemini trả về {} câu thô", batch.sentences().size());
        return batch.sentences();
    }

    /** Ánh xạ mã HTTP của Google thành lỗi có ý nghĩa cho người dùng; không lộ body chứa key. */
    private static ApiException mapHttpError(RestClientResponseException e) {
        int status = e.getStatusCode().value();
        if (status == 429) {
            return new ApiException(HttpStatus.TOO_MANY_REQUESTS, "AI_RATE_LIMIT",
                    "AI đang quá tải (hết hạn mức free tier), thử lại sau ít phút");
        }
        if (status == 400 || status == 401 || status == 403 || status == 404) {
            return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_CONFIG",
                    "GEMINI_API_KEY không hợp lệ hoặc model không tồn tại (HTTP " + status + ")");
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
