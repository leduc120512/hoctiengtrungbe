package com.example.bewebtiengtrung.module.quiz.dto;

import com.example.bewebtiengtrung.module.quiz.entity.AttemptStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Kết quả đầy đủ của một lần làm bài (sau khi nộp).
 *
 * <p><b>BẢO MẬT:</b> danh sách {@code answers} lộ đáp án và giải thích, chỉ trả về cho
 * chủ nhân của lần làm bài. Khi lần làm bài còn IN_PROGRESS thì {@code answers} rỗng.</p>
 */
@Schema(name = "AttemptResultResponse", description = "Kết quả chi tiết của một lần làm bài")
public record AttemptResultResponse(

        @Schema(description = "ID lần làm bài") Long id,

        @Schema(description = "ID đề thi") Long quizId,

        @Schema(description = "Tiêu đề đề thi") String quizTitle,

        @Schema(description = "Trạng thái") AttemptStatus status,

        @Schema(description = "Thời điểm bắt đầu") Instant startedAt,

        @Schema(description = "Thời điểm nộp bài") Instant submittedAt,

        @Schema(description = "Điểm đạt được") Integer score,

        @Schema(description = "Tổng điểm tối đa") Integer maxScore,

        @Schema(description = "Phần trăm điểm đạt được (làm tròn xuống)") Integer scorePercent,

        @Schema(description = "Ngưỡng phần trăm để đạt") Integer passScore,

        @Schema(description = "Số câu trả lời đúng") Integer correctCount,

        @Schema(description = "Tổng số câu hỏi") Integer questionCount,

        @Schema(description = "Có đạt hay không") Boolean passed,

        @Schema(description = "Thời gian làm bài (giây)") Integer durationSeconds,

        @Schema(description = "Kết quả từng câu, kèm đáp án đúng và giải thích")
        List<AttemptAnswerResultResponse> answers
) {
}
