package com.example.bewebtiengtrung.module.wordimport.dto;

/**
 * Lỗi của một dòng khi thực thi nhập (các dòng khác vẫn được xử lý bình thường).
 *
 * @param index vị trí dòng trong {@code rows} gửi lên (bắt đầu từ 0)
 */
public record ImportRowError(
        int index,
        String message
) {
}
