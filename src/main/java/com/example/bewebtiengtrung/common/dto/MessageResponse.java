package com.example.bewebtiengtrung.common.dto;

/**
 * Phản hồi đơn giản chỉ gồm một thông điệp tiếng Việt cho client.
 * Dùng cho các thao tác không cần trả dữ liệu (đăng xuất, huỷ ghi danh, xoá...).
 */
public record MessageResponse(String message) {

    /** Tạo nhanh một thông điệp. */
    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }

    /** Thông điệp thành công mặc định. */
    public static MessageResponse success() {
        return new MessageResponse("Thành công");
    }
}
