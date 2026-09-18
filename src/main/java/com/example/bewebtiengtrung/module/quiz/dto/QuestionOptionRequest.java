package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dữ liệu tạo / cập nhật một phương án trả lời (chỉ dành cho quản trị viên). */
@Schema(name = "QuestionOptionRequest", description = "Dữ liệu tạo hoặc cập nhật phương án trả lời")
public record QuestionOptionRequest(

        @Schema(description = "Nội dung phương án", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Nội dung phương án không được để trống")
        @Size(max = 500, message = "Nội dung phương án không được vượt quá 500 ký tự")
        String content,

        @Schema(description = "Phiên âm pinyin")
        @Size(max = 500, message = "Pinyin không được vượt quá 500 ký tự")
        String pinyin,

        @Schema(description = "Đây có phải đáp án đúng không", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Cờ đáp án không được để trống")
        Boolean isCorrect,

        @Schema(description = "Thứ tự hiển thị, mặc định theo vị trí trong danh sách")
        @Min(value = 0, message = "Thứ tự hiển thị không được âm")
        Integer sortOrder
) {
}
