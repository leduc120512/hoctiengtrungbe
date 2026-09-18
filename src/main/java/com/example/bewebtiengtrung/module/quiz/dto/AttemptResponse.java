package com.example.bewebtiengtrung.module.quiz.dto;

import com.example.bewebtiengtrung.module.quiz.entity.AttemptStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** Thông tin một lần làm bài (không kèm chi tiết từng câu). */
@Schema(name = "AttemptResponse", description = "Thông tin một lần làm bài")
public record AttemptResponse(

        @Schema(description = "ID lần làm bài") Long id,

        @Schema(description = "ID đề thi") Long quizId,

        @Schema(description = "Tiêu đề đề thi") String quizTitle,

        @Schema(description = "Trạng thái") AttemptStatus status,

        @Schema(description = "Thời điểm bắt đầu") Instant startedAt,

        @Schema(description = "Thời điểm nộp bài, null khi đang làm") Instant submittedAt,

        @Schema(description = "Hạn nộp bài, null khi đề không giới hạn thời gian") Instant expiresAt,

        @Schema(description = "Điểm đạt được") Integer score,

        @Schema(description = "Tổng điểm tối đa") Integer maxScore,

        @Schema(description = "Số câu trả lời đúng") Integer correctCount,

        @Schema(description = "Tổng số câu hỏi") Integer questionCount,

        @Schema(description = "Có đạt hay không") Boolean passed,

        @Schema(description = "Thời gian làm bài (giây)") Integer durationSeconds,

        @Schema(description = "Giới hạn thời gian của đề (giây)") Integer timeLimitSeconds
) {
}
