package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Phương án trả lời - PHIÊN BẢN ĐÃ LỘ ĐÁP ÁN.
 *
 * <p><b>BẢO MẬT:</b> chỉ được dùng trong kết quả sau khi nộp bài
 * (AttemptResultResponse) hoặc trong màn hình quản trị. Không bao giờ dùng cho
 * GET /api/v1/quizzes/{id}.</p>
 */
@Schema(name = "QuestionOptionResultResponse", description = "Phương án trả lời kèm cờ đáp án (sau khi nộp bài)")
public record QuestionOptionResultResponse(

        @Schema(description = "ID phương án") Long id,

        @Schema(description = "Nội dung phương án") String content,

        @Schema(description = "Phiên âm pinyin") String pinyin,

        @Schema(description = "Đây có phải đáp án đúng không") Boolean isCorrect,

        @Schema(description = "Thứ tự hiển thị") Integer sortOrder
) {
}
