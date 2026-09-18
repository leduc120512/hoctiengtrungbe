package com.example.bewebtiengtrung.module.quiz.dto;

import com.example.bewebtiengtrung.module.quiz.entity.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Câu hỏi - PHIÊN BẢN ĐÃ LỘ ĐÁP ÁN (correctText, explanation, cờ isCorrect của phương án).
 *
 * <p><b>BẢO MẬT:</b> chỉ dùng ở hai chỗ:
 * (1) kết quả trả về sau khi người dùng nộp bài / xem lại bài đã nộp của chính mình,
 * (2) API quản trị dành cho ROLE_ADMIN.
 * Không bao giờ dùng cho endpoint làm bài GET /api/v1/quizzes/{id}.</p>
 */
@Schema(name = "QuestionResultResponse", description = "Câu hỏi kèm đáp án và giải thích (sau khi nộp bài)")
public record QuestionResultResponse(

        @Schema(description = "ID câu hỏi") Long id,

        @Schema(description = "Loại câu hỏi") QuestionType type,

        @Schema(description = "Đề bài (tiếng Trung)") String prompt,

        @Schema(description = "Phiên âm pinyin của đề bài") String promptPinyin,

        @Schema(description = "Bản dịch tiếng Việt của đề bài") String promptVi,

        @Schema(description = "Đường dẫn file audio") String audioUrl,

        @Schema(description = "Đường dẫn hình ảnh") String imageUrl,

        @Schema(description = "Đáp án dạng chữ (FILL_BLANK / TRANSLATION)") String correctText,

        @Schema(description = "Giải thích đáp án") String explanation,

        @Schema(description = "Số điểm của câu hỏi") Integer points,

        @Schema(description = "Thứ tự hiển thị") Integer sortOrder,

        @Schema(description = "ID từ vựng liên quan, có thể null") Long wordId,

        @Schema(description = "Danh sách phương án kèm cờ đáp án")
        List<QuestionOptionResultResponse> options
) {
}
