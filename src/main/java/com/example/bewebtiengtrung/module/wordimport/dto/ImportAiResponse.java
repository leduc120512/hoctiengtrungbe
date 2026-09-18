package com.example.bewebtiengtrung.module.wordimport.dto;

/**
 * Kết quả AI điền từ: số từ AI trả về, model đã dùng, và bảng duyệt trước y hệt {@code /preview}
 * — frontend đổ thẳng vào bảng duyệt, người học sửa rồi mới {@code /confirm}.
 *
 * @param model   tên model AI đã dùng
 * @param aiWords số từ AI trả về (trước khi duyệt)
 * @param preview bảng duyệt trước của đúng các từ đó
 */
public record ImportAiResponse(
        String model,
        int aiWords,
        ImportPreviewResponse preview
) {
}
