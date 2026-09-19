package com.example.bewebtiengtrung.module.ai.service;

import com.example.bewebtiengtrung.common.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ánh xạ mã HTTP của Google thành lỗi người dùng hiểu được. 404 phải nói rõ là MODEL hỏng (Google ngừng cấp
 * model cũ cho tài khoản mới) chứ không đổ cho key — người dùng từng đi tạo key mới vô ích vì thông báo gộp.
 */
class GeminiHttpErrorTest {

    private static RestClientResponseException http(int status) {
        return new RestClientResponseException("x", status, "x", null, "{}".getBytes(StandardCharsets.UTF_8), null);
    }

    @Test
    void bon_le_bon_neu_ten_model_va_goi_y_doi_bien() {
        ApiException ex = GeminiSentenceGenerator.mapHttpError(http(404), "gemini-2.5-flash");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(ex.getCode()).isEqualTo("AI_CONFIG");
        assertThat(ex.getMessage())
                .contains("gemini-2.5-flash")
                .contains("GEMINI_MODEL")
                .doesNotContain("GEMINI_API_KEY");
    }

    @Test
    void bon_tram_va_bon_le_ba_la_loi_key() {
        for (int status : new int[]{400, 401, 403}) {
            ApiException ex = GeminiSentenceGenerator.mapHttpError(http(status), "m");
            assertThat(ex.getCode()).isEqualTo("AI_CONFIG");
            assertThat(ex.getMessage()).contains("GEMINI_API_KEY").contains("HTTP " + status);
        }
    }

    @Test
    void bon_hai_chin_la_het_han_muc() {
        ApiException ex = GeminiSentenceGenerator.mapHttpError(http(429), "m");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(ex.getCode()).isEqualTo("AI_RATE_LIMIT");
    }

    @Test
    void loi_khac_la_upstream_502() {
        ApiException ex = GeminiSentenceGenerator.mapHttpError(http(500), "m");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(ex.getCode()).isEqualTo("AI_UPSTREAM");
    }
}
