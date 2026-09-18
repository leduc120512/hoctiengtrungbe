package com.example.bewebtiengtrung.module.srs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu tạo/cập nhật một bộ thẻ.
 * {@code slug} để trống thì hệ thống tự sinh từ {@code name}.
 */
@Schema(description = "Dữ liệu tạo hoặc cập nhật bộ thẻ")
public record DeckRequest(

        @Schema(description = "Tên bộ thẻ", example = "HSK 1 - Chào hỏi")
        @NotBlank(message = "Tên bộ thẻ không được để trống")
        @Size(max = 200, message = "Tên bộ thẻ tối đa 200 ký tự")
        String name,

        @Schema(description = "Mô tả ngắn")
        @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
        String description,

        @Schema(description = "Slug tuỳ chọn, bỏ trống sẽ tự sinh từ tên")
        @Size(max = 150, message = "Slug tối đa 150 ký tự")
        String slug,

        @Schema(description = "Cấp độ HSK (1-6)", example = "1")
        @Min(value = 1, message = "Cấp độ HSK nhỏ nhất là 1")
        @Max(value = 6, message = "Cấp độ HSK lớn nhất là 6")
        Integer hskLevel,

        @Schema(description = "Id chủ đề từ vựng gắn kèm")
        Long topicId,

        @Schema(description = "Cho phép người khác xem bộ thẻ")
        Boolean isPublic
) {
}
