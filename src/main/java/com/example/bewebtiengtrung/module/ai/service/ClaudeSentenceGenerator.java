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
import com.example.bewebtiengtrung.module.ai.dto.GeneratedSentence;
import com.example.bewebtiengtrung.module.ai.dto.LearnedWordBrief;
import com.example.bewebtiengtrung.module.ai.dto.SentenceBatch;
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
 * Gọi Claude qua SDK Java chính thức ({@code com.anthropic:anthropic-java}) để sinh câu luyện nghe.
 *
 * <p>Dùng <b>structured output có kiểu</b>: {@code outputConfig(SentenceBatch.class)} — SDK tự sinh
 * JSON schema từ record {@link SentenceBatch} và tự parse câu trả lời về đúng kiểu đó.
 * Không dùng HTTP thô, không prefill assistant, không {@code temperature}, không {@code budget_tokens}.</p>
 *
 * <p>Client được khởi tạo lười (lần gọi đầu tiên) để ứng dụng vẫn khởi động bình thường
 * khi chưa có API key; khi đó {@link #isEnabled()} trả về {@code false} và {@link #generate}
 * ném {@link ApiException} 503.</p>
 *
 * <p><b>Bảo mật:</b> không bao giờ ghi API key ra log; thông điệp lỗi từ SDK được cắt ngắn và
 * lọc mọi chuỗi có dạng khoá {@code sk-ant-…} trước khi trả về client.</p>
 */
@Service
public class ClaudeSentenceGenerator implements AiSentenceGenerator {

    private static final Logger log = LoggerFactory.getLogger(ClaudeSentenceGenerator.class);

    /**
     * System prompt cố định (không đổi giữa các lần gọi) để tận dụng prompt caching.
     * Giữ nguyên văn theo hợp đồng — đây là ràng buộc chất lượng số 1 của tính năng.
     */
    static final String SYSTEM_PROMPT =
            "Bạn là giáo viên tiếng Trung cho người Việt mới học. Nhiệm vụ: viết câu luyện nghe CHỈ dùng "
            + "những chữ Hán trong danh sách từ đã học được cung cấp — tuyệt đối không dùng chữ nào ngoài danh "
            + "sách (kể cả 的, 个, 了, 没, 吗 nếu chúng không có trong danh sách). Câu tự nhiên, đúng ngữ pháp, "
            + "trình độ HSK 1–2. Cấp 1: 3–5 chữ, một chủ ngữ một hành động. Cấp 2: 5–7 chữ, thêm thời gian hoặc "
            + "nơi chốn. Cấp 3: 7–11 chữ, hai vế hoặc đủ giờ giấc. Đổi chủ ngữ, thời gian, hành động; không lặp "
            + "cấu trúc. Pinyin có dấu thanh, viết hoa chữ đầu câu, mỗi từ cách nhau bằng khoảng trắng, giữ dấu "
            + "câu. Nghĩa tiếng Việt tự nhiên.";

    /** Số câu đã có tối đa đưa vào prompt "đừng tạo lại" — tránh prompt phình quá lớn. */
    private static final int MAX_AVOID_IN_PROMPT = 500;

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

    @Override
    public List<GeneratedSentence> generate(List<LearnedWordBrief> words, List<String> avoidHanzi,
                                            int count, Integer level, List<String> focusWords) {
        if (!isEnabled()) {
            throw disabled();
        }
        // Xin dư 1/3 để bù phần bị bộ lọc chữ lạ / trùng loại đi ở tầng service.
        int ask = count + count / 3;
        String userPrompt = buildUserPrompt(words, avoidHanzi, ask, level, focusWords);
        StructuredMessageCreateParams<SentenceBatch> params = buildParams(userPrompt);

        log.info("Gọi Claude {}: xin {} câu (cần {}), {} từ đã học, tránh {} câu đã có",
                props.getModel(), ask, count, words.size(), avoidHanzi == null ? 0 : avoidHanzi.size());
        try {
            StructuredMessage<SentenceBatch> message = client().messages().create(params);
            SentenceBatch batch = message.content().stream()
                    .flatMap(block -> block.text().stream())
                    .map(StructuredTextBlock::text)
                    .findFirst()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_GATEWAY, "AI_EMPTY",
                            "AI không trả về câu nào"));
            List<GeneratedSentence> result = batch.sentences() == null
                    ? List.of()
                    : batch.sentences().stream().filter(Objects::nonNull).toList();
            log.info("Claude trả về {} ứng viên (stop={}, tokens vào/ra = {}/{})",
                    result.size(),
                    message.stopReason().map(Object::toString).orElse("?"),
                    message.usage().inputTokens(), message.usage().outputTokens());
            return result;
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
     * {@link StructuredMessageCreateParams} với cùng kiểu trả về để SDK vẫn parse về {@link SentenceBatch}.
     * Nếu effort cấu hình rỗng/không hợp lệ thì giữ nguyên mặc định của API.</p>
     */
    StructuredMessageCreateParams<SentenceBatch> buildParams(String userPrompt) {
        StructuredMessageCreateParams<SentenceBatch> typed = MessageCreateParams.builder()
                .model(props.getModel())
                .maxTokens(props.getMaxTokens())
                .systemOfTextBlockParams(List.of(TextBlockParam.builder()
                        .text(SYSTEM_PROMPT)
                        .cacheControl(CacheControlEphemeral.builder().build())
                        .build()))
                .addUserMessage(userPrompt)
                .outputConfig(SentenceBatch.class)
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
        return new StructuredMessageCreateParams<>(SentenceBatch.class, rawWithEffort);
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
     * Prompt người dùng: danh sách từ "汉字 (pinyin) nghĩa" cách nhau "; ", số câu cần, cấp,
     * từ cần lặp nhiều (nếu có) và danh sách câu đã có với yêu cầu "đừng tạo lại".
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
        sb.append(" Mỗi câu chỉ được dùng chữ Hán có trong danh sách trên; mỗi câu khác nhau về chủ ngữ, "
                + "thời gian hoặc hành động.\n");

        List<String> focus = focusWords == null ? List.of()
                : focusWords.stream().filter(f -> f != null && !f.isBlank()).map(String::trim).toList();
        if (!focus.isEmpty()) {
            sb.append("Lặp nhiều hơn các từ: ").append(String.join(", ", focus)).append('\n');
        }

        List<String> avoid = avoidHanzi == null ? List.of()
                : avoidHanzi.stream().filter(s -> s != null && !s.isBlank()).map(String::trim).toList();
        if (!avoid.isEmpty()) {
            List<String> shown = avoid.size() > MAX_AVOID_IN_PROMPT ? avoid.subList(0, MAX_AVOID_IN_PROMPT) : avoid;
            sb.append("Các câu đã có (").append(avoid.size()).append(" câu), đừng tạo lại: ")
                    .append(String.join(" ", shown)).append('\n');
        }
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
