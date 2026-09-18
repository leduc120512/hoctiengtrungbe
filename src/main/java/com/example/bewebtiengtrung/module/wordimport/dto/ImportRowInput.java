package com.example.bewebtiengtrung.module.wordimport.dto;

import jakarta.validation.constraints.Size;

/**
 * Một dòng dữ liệu thô người dùng dán vào khi nhập từ vựng.
 *
 * <p>Mọi trường đều tuỳ chọn: thiếu chữ Hán thì hệ thống sẽ tra ngược theo pinyin để gợi ý;
 * thiếu pinyin thì lấy từ từ điển; thiếu nghĩa tiếng Anh thì tự điền từ CC-CEDICT.
 * Chỉ nghĩa tiếng Việt là bắt buộc khi tạo mới (được kiểm tra ở bước preview).</p>
 */
public record ImportRowInput(

        @Size(max = 60, message = "Chữ giản thể tối đa 60 ký tự")
        String simplified,

        @Size(max = 120, message = "Phiên âm pinyin tối đa 120 ký tự")
        String pinyin,

        @Size(max = 500, message = "Nghĩa tiếng Việt tối đa 500 ký tự")
        String meaningVi,

        @Size(max = 500, message = "Nghĩa tiếng Anh tối đa 500 ký tự")
        String meaningEn
) {
}
