package com.example.bewebtiengtrung.module.ai.service;

import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.module.ai.config.AiProperties;
import com.example.bewebtiengtrung.module.ai.dto.CompletedWord;
import com.example.bewebtiengtrung.module.ai.dto.GeneratedSentence;
import com.example.bewebtiengtrung.module.ai.dto.LearnedWordBrief;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Chọn nhà cung cấp AI theo cấu hình {@code app.ai.provider}:
 * <ul>
 *   <li>{@code auto}   — Claude nếu có ANTHROPIC_API_KEY, không thì Gemini nếu có GEMINI_API_KEY, không thì tắt;</li>
 *   <li>{@code claude} / {@code gemini} — ép dùng một bên (tắt nếu thiếu key tương ứng);</li>
 *   <li>{@code off}    — tắt hẳn.</li>
 * </ul>
 * Là bean {@code @Primary} nên tầng service chỉ cần inject {@link AiSentenceGenerator} hoặc
 * {@link AiWordCompleter}; cả hai việc (sinh câu, điền từ) đi cùng một provider.
 */
@Service
@Primary
public class AiSentenceGeneratorRouter implements AiSentenceGenerator, AiWordCompleter {

    /** Tên provider đang hoạt động, dùng cho {@code /api/v1/ai/status}. */
    public enum Provider { CLAUDE, GEMINI, NONE }

    private final AiProperties props;
    private final ClaudeSentenceGenerator claude;
    private final GeminiSentenceGenerator gemini;

    public AiSentenceGeneratorRouter(AiProperties props, ClaudeSentenceGenerator claude,
                                     GeminiSentenceGenerator gemini) {
        this.props = props;
        this.claude = claude;
        this.gemini = gemini;
    }

    /** Provider được chọn theo cấu hình và key hiện có. */
    public Provider activeProvider() {
        String mode = props.getProvider() == null ? "auto" : props.getProvider().trim().toLowerCase(Locale.ROOT);
        return switch (mode) {
            case "claude" -> claude.isEnabled() ? Provider.CLAUDE : Provider.NONE;
            case "gemini" -> gemini.isEnabled() ? Provider.GEMINI : Provider.NONE;
            case "off", "none", "false" -> Provider.NONE;
            default -> claude.isEnabled() ? Provider.CLAUDE
                    : gemini.isEnabled() ? Provider.GEMINI : Provider.NONE;
        };
    }

    /** Thông điệp tiếng Việt giải thích vì sao AI đang tắt (null nếu đang bật). */
    public String disabledReason() {
        return switch (activeProvider()) {
            case NONE -> "Chưa cấu hình AI: đặt GEMINI_API_KEY (miễn phí, aistudio.google.com/apikey) "
                    + "hoặc ANTHROPIC_API_KEY trên máy chủ";
            default -> null;
        };
    }

    private AiSentenceGenerator delegate() {
        return switch (activeProvider()) {
            case CLAUDE -> claude;
            case GEMINI -> gemini;
            case NONE -> null;
        };
    }

    @Override
    public boolean isEnabled() {
        return activeProvider() != Provider.NONE;
    }

    @Override
    public String model() {
        return switch (activeProvider()) {
            case CLAUDE -> claude.model();
            case GEMINI -> gemini.model();
            case NONE -> "";
        };
    }

    @Override
    public List<GeneratedSentence> generate(List<LearnedWordBrief> words, List<String> avoidHanzi,
                                            int count, Integer level, List<String> focusWords) {
        AiSentenceGenerator target = delegate();
        if (target == null) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_DISABLED", disabledReason());
        }
        return target.generate(words, avoidHanzi, count, level, focusWords);
    }

    @Override
    public List<CompletedWord> completeWords(String text, int hskLevel, List<String> learnedHanzi, int maxWords) {
        AiWordCompleter target = switch (activeProvider()) {
            case CLAUDE -> claude;
            case GEMINI -> gemini;
            case NONE -> null;
        };
        if (target == null) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_DISABLED", disabledReason());
        }
        return target.completeWords(text, hskLevel, learnedHanzi, maxWords);
    }
}
