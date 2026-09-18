package com.example.bewebtiengtrung.module.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cấu hình gọi Claude, ánh xạ nhánh {@code app.ai.*} trong {@code application.yml}.
 *
 * <p>Mọi trường đều có giá trị mặc định trong code nên yml không bắt buộc phải khai báo.
 * Riêng {@link #apiKey} được đọc từ biến môi trường {@code ANTHROPIC_API_KEY}
 * (qua {@code @Value}); nếu yml có đặt {@code app.ai.api-key} thì binder sẽ ghi đè.
 * Không bao giờ ghi khoá này ra log.</p>
 *
 * <p>Đăng ký bằng {@code @EnableConfigurationProperties(AiProperties.class)} trong {@link AiConfig}.</p>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    /** Tên biến môi trường chứa API key của Anthropic. */
    public static final String API_KEY_ENV = "ANTHROPIC_API_KEY";

    /** API key Anthropic — rỗng ⇒ tính năng AI bị tắt. */
    @Value("${ANTHROPIC_API_KEY:}")
    private String apiKey = "";

    /**
     * Nhà cung cấp AI: {@code auto} (Claude nếu có ANTHROPIC_API_KEY, không thì Gemini nếu có
     * GEMINI_API_KEY, không thì tắt), {@code claude}, {@code gemini} hoặc {@code off}.
     */
    private String provider = "auto";

    /** Tên biến môi trường chứa API key của Google AI Studio (Gemini, có free tier). */
    public static final String GEMINI_KEY_ENV = "GEMINI_API_KEY";

    /** API key Gemini — rỗng ⇒ không dùng Gemini. */
    @Value("${GEMINI_API_KEY:}")
    private String geminiApiKey = "";

    /** Model Gemini dùng để sinh câu (free tier). */
    private String geminiModel = "gemini-2.5-flash";

    /** Model Claude dùng để sinh câu. */
    private String model = "claude-opus-5";

    /** Mức nỗ lực suy luận ({@code low}/{@code medium}/{@code high}/{@code xhigh}); rỗng ⇒ mặc định của API. */
    private String effort = "medium";

    /** Số token tối đa cho một câu trả lời. */
    private int maxTokens = 16000;

    /** Thời gian chờ tối đa cho một lần gọi, tính bằng giây. */
    private int timeoutSeconds = 120;

    /**
     * API key thực tế sẽ dùng: giá trị đã bind (yml hoặc {@code @Value}), nếu rỗng thì
     * thử đọc thẳng {@code System.getenv} để chạy được cả khi Spring Environment chưa nạp biến.
     */
    public String resolvedApiKey() {
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey.trim();
        }
        String fromEnv = System.getenv(API_KEY_ENV);
        return fromEnv == null ? "" : fromEnv.trim();
    }

    /** Đã cấu hình API key hay chưa. */
    public boolean hasApiKey() {
        return !resolvedApiKey().isEmpty();
    }

    /** API key Gemini thực tế: giá trị đã bind, nếu rỗng thì đọc thẳng {@code System.getenv}. */
    public String resolvedGeminiKey() {
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            return geminiApiKey.trim();
        }
        String fromEnv = System.getenv(GEMINI_KEY_ENV);
        return fromEnv == null ? "" : fromEnv.trim();
    }

    /** Đã cấu hình GEMINI_API_KEY hay chưa. */
    public boolean hasGeminiKey() {
        return !resolvedGeminiKey().isEmpty();
    }
}
