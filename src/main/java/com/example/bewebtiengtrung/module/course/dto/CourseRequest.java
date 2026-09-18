package com.example.bewebtiengtrung.module.course.dto;

import com.example.bewebtiengtrung.module.course.entity.CourseLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dữ liệu tạo/cập nhật khóa học (chỉ admin). */
public record CourseRequest(
        @NotBlank(message = "Slug không được để trống")
        @Size(max = 150, message = "Slug tối đa 150 ký tự")
        String slug,

        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
        String title,

        String description,

        @NotNull(message = "Cấp độ HSK không được để trống")
        CourseLevel level,

        @Size(max = 500, message = "Đường dẫn ảnh tối đa 500 ký tự")
        String thumbnailUrl,

        Boolean published,

        @Min(value = 0, message = "Thứ tự sắp xếp không được âm")
        Integer sortOrder,

        @Min(value = 0, message = "Thời lượng ước tính không được âm")
        Integer estimatedMinutes
) {
}
