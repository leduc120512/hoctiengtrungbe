package com.example.bewebtiengtrung.common.dto;

/**
 * Phản hồi chỉ chứa khoá chính của bản ghi vừa được tạo.
 * Dùng cho các endpoint {@code POST} khi client chỉ cần biết id.
 */
public record IdResponse(Long id) {

    /** Tạo nhanh một phản hồi id. */
    public static IdResponse of(Long id) {
        return new IdResponse(id);
    }
}
