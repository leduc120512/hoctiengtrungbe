package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Phương án trả lời - PHIÊN BẢN CÔNG KHAI (an toàn).
 *
 * <p><b>BẢO MẬT:</b> record này CỐ Ý không có trường {@code isCorrect}.
 * Nếu thêm cờ đáp án vào đây thì bất kỳ ai gọi GET /api/v1/quizzes/{id} cũng biết đáp án
 * trước khi làm bài. Muốn lộ đáp án hãy dùng {@link QuestionOptionResultResponse}.</p>
 */
@Schema(name = "QuestionOptionResponse", description = "Phương án trả lời (không lộ đáp án)")
public record QuestionOptionResponse(

        @Schema(description = "ID phương án") Long id,

        @Schema(description = "Nội dung phương án") String content,

        @Schema(description = "Phiên âm pinyin") String pinyin,

        @Schema(description = "Thứ tự hiển thị") Integer sortOrder
) {
}
