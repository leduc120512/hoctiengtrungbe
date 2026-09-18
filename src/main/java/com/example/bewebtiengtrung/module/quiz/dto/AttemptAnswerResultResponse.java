package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Kết quả chấm của một câu hỏi trong lần làm bài.
 *
 * <p><b>BẢO MẬT:</b> có chứa {@link QuestionResultResponse} nên lộ đáp án - chỉ trả về
 * cho chính chủ nhân của lần làm bài SAU KHI đã nộp.</p>
 */
@Schema(name = "AttemptAnswerResultResponse", description = "Kết quả chấm của một câu hỏi")
public record AttemptAnswerResultResponse(

        @Schema(description = "ID câu hỏi") Long questionId,

        @Schema(description = "ID phương án người dùng đã chọn, null nếu bỏ trống hoặc câu tự luận")
        Long selectedOptionId,

        @Schema(description = "Đáp án dạng chữ người dùng đã nhập") String textAnswer,

        @Schema(description = "Câu này trả lời đúng hay sai") Boolean isCorrect,

        @Schema(description = "Điểm được cộng cho câu này") Integer pointsAwarded,

        @Schema(description = "Thời điểm ghi nhận câu trả lời") Instant answeredAt,

        @Schema(description = "Nội dung câu hỏi kèm đáp án và giải thích")
        QuestionResultResponse question
) {
}
