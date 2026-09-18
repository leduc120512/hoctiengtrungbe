package com.example.bewebtiengtrung.module.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dữ liệu tạo/cập nhật điểm ngữ pháp của bài học (chỉ admin). */
public record LessonGrammarRequest(
        @NotBlank(message = "Tiêu đề điểm ngữ pháp không được để trống")
        @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
        String title,

        @Size(max = 500, message = "Cấu trúc tối đa 500 ký tự")
        String structure,

        String explanation,

        @Size(max = 500, message = "Ví dụ tiếng Trung tối đa 500 ký tự")
        String exampleZh,

        @Size(max = 800, message = "Ví dụ tiếng Việt tối đa 800 ký tự")
        String exampleVi,

        @Min(value = 0, message = "Thứ tự sắp xếp không được âm")
        Integer sortOrder
) {
}
