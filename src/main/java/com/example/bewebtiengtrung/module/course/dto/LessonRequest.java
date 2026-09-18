package com.example.bewebtiengtrung.module.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dữ liệu tạo/cập nhật bài học (chỉ admin). */
public record LessonRequest(
        @NotNull(message = "Khóa học không được để trống")
        Long courseId,

        @NotBlank(message = "Slug không được để trống")
        @Size(max = 150, message = "Slug tối đa 150 ký tự")
        String slug,

        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
        String title,

        @Size(max = 1000, message = "Tóm tắt tối đa 1000 ký tự")
        String summary,

        String content,

        @Size(max = 500, message = "Đường dẫn video tối đa 500 ký tự")
        String videoUrl,

        @Size(max = 500, message = "Đường dẫn audio tối đa 500 ký tự")
        String audioUrl,

        @Min(value = 0, message = "Thứ tự sắp xếp không được âm")
        Integer sortOrder,

        @Min(value = 0, message = "Thời lượng ước tính không được âm")
        Integer estimatedMinutes,

        Boolean published
) {
}
