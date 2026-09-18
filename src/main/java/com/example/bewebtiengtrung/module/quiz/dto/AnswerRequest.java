package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Một câu trả lời do người dùng gửi lên.
 *
 * <p>Với câu trắc nghiệm dùng {@code selectedOptionId}; với FILL_BLANK / TRANSLATION
 * dùng {@code textAnswer}. Câu bỏ trống có thể không gửi lên, hệ thống tính là sai.</p>
 */
@Schema(name = "AnswerRequest", description = "Câu trả lời cho một câu hỏi")
public record AnswerRequest(

        @Schema(description = "ID câu hỏi", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "questionId không được để trống")
        Long questionId,

        @Schema(description = "ID phương án đã chọn (câu trắc nghiệm)")
        Long selectedOptionId,

        @Schema(description = "Đáp án dạng chữ (câu điền từ / dịch câu)")
        @Size(max = 500, message = "Đáp án không được vượt quá 500 ký tự")
        String textAnswer
) {
}
