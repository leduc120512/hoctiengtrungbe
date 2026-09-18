package com.example.bewebtiengtrung.module.ai.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.errors.AnthropicException;
import com.anthropic.errors.AnthropicServiceException;
import com.anthropic.errors.PermissionDeniedException;
import com.anthropic.errors.RateLimitException;
import com.anthropic.errors.UnauthorizedException;
import com.anthropic.models.messages.CacheControlEphemeral;
import com.anthropic.models.messages.JsonOutputFormat;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.StructuredMessage;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.anthropic.models.messages.StructuredTextBlock;
import com.anthropic.models.messages.TextBlockParam;
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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Gọi Claude qua SDK Java chính thức ({@code com.anthropic:anthropic-java}) cho hai việc:
 * sinh câu luyện nghe ({@link AiSentenceGenerator}) và điền từ ({@link AiWordCompleter}).
 *
 * <p>Dùng <b>structured output có kiểu</b>: {@code outputConfig(Class)} — SDK tự sinh JSON schema
 * từ record ({@link SentenceBatch} / {@link WordBatch}) và tự parse câu trả lời về đúng kiểu đó.
 * Không dùng HTTP thô, không prefill assistant, không {@code temperature}, không {@code budget_tokens}.</p>
 *
 * <p>Client được khởi tạo lười (lần gọi đầu tiên) để ứng dụng vẫn khởi động bình thường
 * khi chưa có API key; khi đó {@link #isEnabled()} trả về {@code false} và các hàm gọi
 * ném {@link ApiException} 503.</p>
 *
 * <p><b>Bảo mật:</b> không bao giờ ghi API key ra log; thông điệp lỗi từ SDK được cắt ngắn và
 * lọc mọi chuỗi có dạng khoá {@code sk-ant-…} trước khi trả về client.</p>
 */
@Service
public class ClaudeSentenceGenerator implements AiSentenceGenerator, AiWordCompleter {

    private static final Logger log = LoggerFactory.getLogger(ClaudeSentenceGenerator.class);

    /**
     * System prompt sinh câu, cố định giữa các lần gọi để tận dụng prompt caching.
     *
     * <p>Ràng buộc chất lượng số 1 vẫn là "chỉ dùng chữ đã học". Ràng buộc số 2 đến từ chính người học:
     * câu mới phải là một CÁCH GHÉP MỚI của nhiều từ đã học, không phải một câu cũ bị đổi chỗ — vì mục
     * đích là gặp lại từ trong ngữ cảnh khác để nhớ, chứ không phải có thêm số câu.</p>
     */
    static final String SYSTEM_PROMPT =
            "Bạn là giáo viên tiếng Trung cho người Việt mới học. Nhiệm vụ: viết câu luyện nghe CHỈ dùng "
            + "những chữ Hán trong danh sách từ đã học được cung cấp — tuyệt đối không dùng chữ nào ngoài danh "
            + "sách (kể cả 的, 个, 了, 没, 吗 nếu chúng không có trong danh sách). "
            + "Mục đích của người học là ÔN TỪ bằng cách gặp lại chúng trong ngữ cảnh mới, nên mỗi câu phải là "
            + "một CÁCH GHÉP MỚI của ít nhất 2–3 từ khác nhau trong danh sách. Tuyệt đối không lấy một câu đã có "
            + "rồi đổi chỗ các từ, thay một từ, thêm bớt dấu câu hay đổi chủ ngữ cho có — câu như vậy vô ích. "
            + "Trong cùng một đợt, hai câu không được dùng cùng một bộ từ. Đa dạng mẫu câu: khẳng định, phủ định, "
            + "câu hỏi (dùng 吗/什么/几/哪儿… nếu có trong danh sách), có thời gian, có nơi chốn, hai vế nối nhau. "
            + "Câu tự nhiên, đúng ngữ pháp, trình độ HSK 1–2. Cấp 1: 3–5 chữ, một chủ ngữ một hành động. "
            + "Cấp 2: 5–7 chữ, thêm thời gian hoặc nơi chốn. Cấp 3: 7–11 chữ, hai vế hoặc đủ giờ giấc. "
            + "Pinyin có dấu thanh, viết hoa chữ đầu câu, mỗi từ cách nhau bằng khoảng trắng, giữ dấu câu. "
            + "Nghĩa tiếng Việt tự nhiên.";

    /**
     * System prompt điền từ. Người học gõ rất tuỳ tiện — chỉ tiếng Việt, chỉ pinyin không dấu, hay một
     * câu "gợi ý 10 từ về…" — AI phải trả về từng từ đủ bốn phần, giữ nguyên phần người học đã ghi.
     */
    static final String WORD_SYSTEM_PROMPT =
            "Bạn là giáo viên tiếng Trung cho người Việt. Người học đưa một danh sách từ muốn thêm vào sổ từ, "
            + "viết rất tuỳ tiện: có dòng chỉ có chữ Hán, có dòng chỉ có pinyin (có hoặc không dấu), có dòng chỉ "
            + "có tiếng Việt, có dòng đủ cả, có khi là một câu tiếng Việt như 'gợi ý 10 từ về đồ ăn'. "
            + "Nhiệm vụ: trả về danh sách từ đã ĐIỀN ĐỦ bốn phần cho mỗi từ: simplified (chữ Hán giản thể chuẩn "
            + "từ điển, không dấu câu), pinyin (có dấu thanh, chữ thường, các âm tiết cách nhau bằng khoảng trắng, "
            + "ví dụ 'xué xí'), meaningVi (nghĩa tiếng Việt ngắn gọn, nghĩa thường dùng nhất), meaningEn (nghĩa "
            + "tiếng Anh ngắn). Quy tắc: giữ đúng thứ tự và số từ người học đưa — mỗi dòng là một từ, đừng tách "
            + "một từ thành nhiều từ hay gộp hai dòng; phần người học đã ghi thì giữ nguyên (chỉ sửa lỗi dấu thanh "
            + "hiển nhiên), chỉ điền phần còn thiếu; tiếng Việt mơ hồ thì chọn từ HSK thông dụng nhất ở cấp được "
            + "nêu; chỉ khi người học yêu cầu gợi ý thì mới tự thêm từ, và khi đó không gợi ý lại từ đã học. "
            + "Bỏ qua dòng không phải từ (tiêu đề, số thứ tự trống). Không giải thích gì thêm.";

    /** Số câu đã có tối đa đưa vào prompt "đừng tạo lại" — tránh prompt phình quá lớn. */
    private static final int MAX_AVOID_IN_PROMPT = 500;

    /** Số từ đã học tối đa đưa vào prompt điền từ (chỉ để tránh gợi ý lại). */
    private static final int MAX_LEARNED_IN_WORD_PROMPT = 800;

    /** Độ dài tối đa của thông điệp lỗi SDK được phép lộ ra ngoài. */
    private static final int MAX_ERROR_DETAIL = 200;

    /** Mọi chuỗi trông giống API key đều bị che trước khi ghi log / trả về. */
    private static final Pattern SECRET_PATTERN = Pattern.compile("sk-ant-[A-Za-z0-9_\\-]+");

    private final AiProperties props;

    /** Client khởi tạo lười; {@code volatile} + double-checked locking để an toàn đa luồng. */
    private volatile AnthropicClient client;

    public ClaudeSentenceGenerator(AiProperties props) {
        this.props = props;
    }

    @Override
    public boolean isEnabled() {
        return props.hasApiKey();
    }

    @Override
    public String model() {
        return props.getModel();
    }

    // ------------------------------------------------------------------
    // Sinh câu
    // ------------------------------------------------------------------

    @Override
    public List<GeneratedSentence> generate(List<LearnedWordBrief> words, List<String> avoidHanzi,
                                            int count, Integer level, List<String> focusWords) {
        if (!isEnabled()) {
            throw disabled();
        }
        // Xin dư một nửa để bù phần bị bộ lọc chữ lạ / trùng / đổi chỗ loại đi ở tầng service.
        int ask = askCount(count);
        String userPrompt = buildUserPrompt(words, avoidHanzi, ask, level, focusWords);

        log.info("Gọi Claude {}: xin {} câu (cần {}), {} từ đã học, {} từ ưu tiên, tránh {} câu đã có",
                props.getModel(), ask, count, words.size(),
                focusWords == null ? 0 : focusWords.size(), avoidHanzi == null ? 0 : avoidHanzi.size());
        SentenceBatch batch = callStructured(buildParams(SYSTEM_PROMPT, userPrompt, SentenceBatch.class));
        List<GeneratedSentence> result = batch.sentences() == null
                ? List.of()
                : batch.sentences().stream().filter(Objects::nonNull).toList();
        log.info("Claude trả về {} ứng viên câu", result.size());
        return result;
    }

    /** Số câu xin AI để sau khi lọc còn đủ {@code count}: dư một nửa, ít nhất dư 2. */
    static int askCount(int count) {
        return count + Math.max(2, count / 2);
    }

    // ------------------------------------------------------------------
    // Điền từ
    // ------------------------------------------------------------------

    @Override
    public List<CompletedWord> completeWords(String text, int hskLevel, List<String> learnedHanzi, int maxWords) {
        if (!isEnabled()) {
            throw disabled();
        }
        String userPrompt = buildWordPrompt(text, hskLevel, learnedHanzi, maxWords);
        log.info("Gọi Claude {} điền từ: {} ký tự, cấp HSK {}, tối đa {} từ",
                props.getModel(), text == null ? 0 : text.length(), hskLevel, maxWords);
        WordBatch batch = callStructured(buildParams(WORD_SYSTEM_PROMPT, userPrompt, WordBatch.class));
        List<CompletedWord> result = batch.words() == null
                ? List.of()
                : batch.words().stream().filter(Objects::nonNull).toList();
        log.info("Claude trả về {} từ", result.size());
        return result;
    }

    // ------------------------------------------------------------------
    // Dựng request
    // ------------------------------------------------------------------

    /**
     * Dựng tham số gọi API với structured output có kiểu.
     *
     * <p>{@code outputConfig(Class)} tự sinh lược đồ JSON nhưng không nhận {@code effort}, và hàm
     * sinh lược đồ của SDK ({@code StructuredOutputsKt.outputFormatFromClass}) bị đánh dấu
     * {@code @JvmSynthetic} nên Java không gọi được. Cách làm ở đây chỉ dùng API public: build xong,
     * đọc lại {@link OutputConfig} (đã chứa lược đồ) từ {@code rawParams()}, dựng lại một OutputConfig
     * gồm <i>cùng lược đồ đó</i> cộng thêm effort rồi bọc lại thành
     * {@link StructuredMessageCreateParams} với cùng kiểu trả về để SDK vẫn parse về {@code type}.
     * Nếu effort cấu hình rỗng/không hợp lệ thì giữ nguyên mặc định của API.</p>
     */
    <T> StructuredMessageCreateParams<T> buildParams(String systemPrompt, String userPrompt, Class<T> type) {
        StructuredMessageCreateParams<T> typed = MessageCreateParams.builder()
                .model(props.getModel())
                .maxTokens(props.getMaxTokens())
                .systemOfTextBlockParams(List.of(TextBlockParam.builder()
                        .text(systemPrompt)
                        .cacheControl(CacheControlEphemeral.builder().build())
                        .build()))
                .addUserMessage(userPrompt)
                .outputConfig(type)
                .build();

        OutputConfig.Effort effort = effortOf(props.getEffort());
        if (effort == null) {
            return typed;
        }
        MessageCreateParams raw = typed.rawParams();
        Optional<JsonOutputFormat> format = raw.outputConfig().flatMap(OutputConfig::format);
        if (format.isEmpty()) {
            // Không thấy lược đồ (không mong đợi) — thà bỏ effort còn hơn mất structured output.
            log.warn("Không đọc được lược đồ structured output từ SDK, bỏ qua effort");
            return typed;
        }
        OutputConfig withEffort = OutputConfig.builder()
                .effort(effort)
                .format(format.get())
                .build();
        MessageCreateParams rawWithEffort = raw.toBuilder().outputConfig(withEffort).build();
        return new StructuredMessageCreateParams<>(type, rawWithEffort);
    }

    /** Gọi API và lấy khối kết quả đã parse; mọi lỗi SDK đổi thành {@link ApiException} có mã rõ ràng. */
    private <T> T callStructured(StructuredMessageCreateParams<T> params) {
        try {
            StructuredMessage<T> message = client().messages().create(params);
            T parsed = message.content().stream()
                    .flatMap(block -> block.text().stream())
                    .map(StructuredTextBlock::text)
                    .findFirst()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_GATEWAY, "AI_EMPTY",
                            "AI không trả về nội dung nào"));
            log.info("Claude xong (stop={}, tokens vào/ra = {}/{})",
                    message.stopReason().map(Object::toString).orElse("?"),
                    message.usage().inputTokens(), message.usage().outputTokens());
            return parsed;
        } catch (ApiException e) {
            throw e;
        } catch (UnauthorizedException | PermissionDeniedException e) {
            // SDK 2.34 không có AuthenticationException; 401/403 tương ứng hai lớp này.
            log.warn("Claude từ chối API key (HTTP {})", e.statusCode());
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_AUTH", "API key không hợp lệ");
        } catch (RateLimitException e) {
            log.warn("Claude báo quá tải (HTTP {})", e.statusCode());
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "AI_RATE_LIMIT", "AI đang quá tải, thử lại sau");
        } catch (AnthropicServiceException e) {
            log.error("Dịch vụ AI trả lỗi HTTP {}: {}", e.statusCode(), sanitize(e.getMessage()));
            throw new ApiException(HttpStatus.BAD_GATEWAY, "AI_SERVICE_ERROR",
                    "Lỗi từ dịch vụ AI: " + sanitize(e.getMessage()));
        } catch (AnthropicException e) {
            // Lỗi mạng (AnthropicIoException) hoặc dữ liệu trả về không parse được.
            log.error("Không gọi được dịch vụ AI: {}", sanitize(e.getMessage()));
            throw new ApiException(HttpStatus.BAD_GATEWAY, "AI_UNAVAILABLE",
                    "Không gọi được dịch vụ AI: " + sanitize(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Lỗi không mong đợi khi gọi AI", e);
            throw new ApiException(HttpStatus.BAD_GATEWAY, "AI_ERROR", "Lỗi từ dịch vụ AI");
        }
    }

    /** Ánh xạ chuỗi cấu hình sang hằng effort của SDK; null nếu rỗng hoặc không hợp lệ. */
    static OutputConfig.Effort effortOf(String configured) {
        if (configured == null || configured.isBlank()) {
            return null;
        }
        switch (configured.trim().toLowerCase(Locale.ROOT)) {
            case "low":
                return OutputConfig.Effort.LOW;
            case "medium":
                return OutputConfig.Effort.MEDIUM;
            case "high":
                return OutputConfig.Effort.HIGH;
            case "xhigh":
                return OutputConfig.Effort.XHIGH;
            default:
                log.warn("app.ai.effort='{}' không hợp lệ (low/medium/high/xhigh) — dùng mặc định của API", configured);
                return null;
        }
    }

    /**
     * Prompt người dùng cho việc sinh câu: danh sách từ "汉字 (pinyin) nghĩa" cách nhau "; ", số câu cần,
     * cấp, nhóm từ mới cần ưu tiên (nếu có) và danh sách câu đã có với yêu cầu "đừng tạo lại, đừng đổi chỗ".
     */
    static String buildUserPrompt(List<LearnedWordBrief> words, List<String> avoidHanzi,
                                  int ask, Integer level, List<String> focusWords) {
        StringBuilder sb = new StringBuilder(2048);

        sb.append("Danh sách từ đã học (").append(words.size()).append(" từ): ");
        sb.append(words.stream()
                .map(w -> w.hanzi() + " (" + nullToEmpty(w.pinyin()) + ") " + nullToEmpty(w.meaningVi()))
                .collect(Collectors.joining("; ")));
        sb.append('\n');

        sb.append("Hãy viết ").append(ask).append(" câu");
        if (level != null) {
            sb.append(", tất cả ở cấp ").append(level).append('.');
        } else {
            sb.append(", trộn đều 3 cấp (mỗi cấp khoảng một phần ba).");
        }
        sb.append(" Mỗi câu chỉ được dùng chữ Hán có trong danh sách trên, và phải ghép ít nhất 2–3 từ "
                + "khác nhau của danh sách theo một cách chưa có ở các câu đã có; mỗi câu khác nhau về bộ từ "
                + "dùng, không chỉ khác chủ ngữ hay thứ tự.\n");

        List<String> focus = focusWords == null ? List.of()
                : focusWords.stream().filter(f -> f != null && !f.isBlank()).map(String::trim).toList();
        if (!focus.isEmpty()) {
            sb.append("Từ MỚI cần ưu tiên — mỗi câu phải chứa ít nhất một từ trong nhóm này và ghép nó với "
                    + "các từ đã học khác: ").append(String.join(", ", focus)).append('\n');
        }

        List<String> avoid = avoidHanzi == null ? List.of()
                : avoidHanzi.stream().filter(s -> s != null && !s.isBlank()).map(String::trim).toList();
        if (!avoid.isEmpty()) {
            List<String> shown = avoid.size() > MAX_AVOID_IN_PROMPT ? avoid.subList(0, MAX_AVOID_IN_PROMPT) : avoid;
            sb.append("Các câu đã có (").append(avoid.size())
                    .append(" câu) — đừng tạo lại, cũng đừng chỉ đổi chỗ hay thay một từ trong các câu này: ")
                    .append(String.join(" ", shown)).append('\n');
        }
        return sb.toString();
    }

    /** Prompt người dùng cho việc điền từ: cấp mục tiêu, giới hạn số từ, từ đã học, rồi nội dung thô. */
    static String buildWordPrompt(String text, int hskLevel, List<String> learnedHanzi, int maxWords) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("Cấp HSK mục tiêu: ").append(hskLevel).append(". Trả về tối đa ").append(maxWords).append(" từ.\n");
        List<String> learned = learnedHanzi == null ? List.of()
                : learnedHanzi.stream().filter(s -> s != null && !s.isBlank()).map(String::trim).toList();
        if (!learned.isEmpty()) {
            List<String> shown = learned.size() > MAX_LEARNED_IN_WORD_PROMPT
                    ? learned.subList(0, MAX_LEARNED_IN_WORD_PROMPT) : learned;
            sb.append("Từ đã học (").append(learned.size()).append(" từ, không gợi ý lại): ")
                    .append(String.join(" ", shown)).append('\n');
        }
        sb.append("Danh sách người học đưa:\n").append(text == null ? "" : text.trim()).append('\n');
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Hạ tầng
    // ------------------------------------------------------------------

    /** Lấy client, khởi tạo lười ở lần gọi đầu tiên. */
    private AnthropicClient client() {
        AnthropicClient local = client;
        if (local == null) {
            synchronized (this) {
                local = client;
                if (local == null) {
                    local = AnthropicOkHttpClient.builder()
                            .apiKey(props.resolvedApiKey())
                            .timeout(Duration.ofSeconds(Math.max(1, props.getTimeoutSeconds())))
                            .build();
                    client = local;
                }
            }
        }
        return local;
    }

    /** Lỗi 503 chuẩn khi chưa cấu hình API key. */
    static ApiException disabled() {
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_DISABLED",
                "Chưa cấu hình ANTHROPIC_API_KEY trên máy chủ");
    }

    /** Cắt ngắn và che khoá bí mật trong thông điệp lỗi trước khi lộ ra ngoài. */
    static String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return "không rõ nguyên nhân";
        }
        String masked = SECRET_PATTERN.matcher(message).replaceAll("sk-ant-***");
        String oneLine = masked.replaceAll("\\s+", " ").trim();
        return oneLine.length() > MAX_ERROR_DETAIL ? oneLine.substring(0, MAX_ERROR_DETAIL) + "…" : oneLine;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
