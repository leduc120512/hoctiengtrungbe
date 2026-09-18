package com.example.bewebtiengtrung.module.sentence.dto;

/**
 * Trạng thái cấu hình AI trên máy chủ.
 *
 * @param enabled đã có API key hay chưa
 * @param model   tên model sẽ dùng
 * @param reason  lý do (tiếng Việt) khi {@code enabled = false}; null khi bật
 */
public record AiStatusResponse(
        boolean enabled,
        String provider,
        String model,
        String reason
) {
}
