package com.example.bewebtiengtrung.module.vocabulary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu cập nhật toàn phần một chủ đề từ vựng (chỉ quản trị viên được dùng).
 */
public record UpdateTopicRequest(

        @NotBlank(message = "Slug không được để trống")
        @Size(max = 120, message = "Slug tối đa 120 ký tự")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "Slug chỉ gồm chữ thường, số và dấu gạch ngang")
        String slug,

        @NotBlank(message = "Tên chủ đề tiếng Việt không được để trống")
        @Size(max = 150, message = "Tên chủ đề tiếng Việt tối đa 150 ký tự")
        String nameVi,

        @Size(max = 150, message = "Tên chủ đề tiếng Trung tối đa 150 ký tự")
        String nameZh,

        @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
        String description,

        @Size(max = 100, message = "Tên icon tối đa 100 ký tự")
        String icon,

        @Min(value = 0, message = "Thứ tự sắp xếp không được âm")
        Integer sortOrder
) {
}
