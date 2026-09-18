package com.example.bewebtiengtrung.module.quiz.dto;

import com.example.bewebtiengtrung.module.quiz.entity.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Câu hỏi - PHIÊN BẢN CÔNG KHAI (an toàn), dùng cho GET /api/v1/quizzes/{id}.
 *
 * <p><b>BẢO MẬT - TUYỆT ĐỐI KHÔNG THÊM VÀO ĐÂY:</b>
 * {@code isCorrect} (cờ đáp án của phương án), {@code correctText} (đáp án dạng chữ)
 * và {@code explanation} (giải thích). Ba trường này để lộ toàn bộ đáp án của đề thi.
 * Chúng chỉ được trả về SAU KHI người dùng nộp bài, qua {@link QuestionResultResponse}.</p>
 */
@Schema(name = "QuestionResponse", description = "Câu hỏi khi làm bài (đã ẩn toàn bộ đáp án)")
public record QuestionResponse(

        @Schema(description = "ID câu hỏi") Long id,

        @Schema(description = "Loại câu hỏi") QuestionType type,

        @Schema(description = "Đề bài (tiếng Trung)") String prompt,

        @Schema(description = "Phiên âm pinyin của đề bài") String promptPinyin,

        @Schema(description = "Bản dịch tiếng Việt của đề bài") String promptVi,

        @Schema(description = "Đường dẫn file audio (câu nghe hiểu)") String audioUrl,

        @Schema(description = "Đường dẫn hình ảnh (câu nhìn hình chọn từ)") String imageUrl,

        @Schema(description = "Số điểm của câu hỏi") Integer points,

        @Schema(description = "Thứ tự hiển thị") Integer sortOrder,

        @Schema(description = "ID từ vựng liên quan, có thể null") Long wordId,

        @Schema(description = "Danh sách phương án, không kèm cờ đáp án")
        List<QuestionOptionResponse> options
) {
}
