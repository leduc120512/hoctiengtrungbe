package com.example.bewebtiengtrung.module.srs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dữ liệu tạo/cập nhật một thẻ ghi nhớ. */
@Schema(description = "Dữ liệu tạo hoặc cập nhật thẻ ghi nhớ")
public record FlashcardRequest(

        @Schema(description = "Id từ vựng liên kết (tuỳ chọn)")
        Long wordId,

        @Schema(description = "Mặt trước của thẻ", example = "你好")
        @NotBlank(message = "Mặt trước không được để trống")
        @Size(max = 500, message = "Mặt trước tối đa 500 ký tự")
        String front,

        @Schema(description = "Mặt sau của thẻ", example = "nǐ hǎo - xin chào")
        @NotBlank(message = "Mặt sau không được để trống")
        @Size(max = 1000, message = "Mặt sau tối đa 1000 ký tự")
        String back,

        @Schema(description = "Gợi ý")
        @Size(max = 500, message = "Gợi ý tối đa 500 ký tự")
        String hint,

        @Schema(description = "Đường dẫn file phát âm")
        @Size(max = 500, message = "Đường dẫn audio tối đa 500 ký tự")
        String audioUrl,

        @Schema(description = "Đường dẫn ảnh minh hoạ")
        @Size(max = 500, message = "Đường dẫn ảnh tối đa 500 ký tự")
        String imageUrl,

        @Schema(description = "Thứ tự hiển thị trong bộ thẻ")
        Integer sortOrder
) {
}
