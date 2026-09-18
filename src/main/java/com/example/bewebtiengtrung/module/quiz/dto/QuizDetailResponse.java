package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Chi tiết đề kiểm tra dùng để LÀM BÀI.
 *
 * <p><b>BẢO MẬT:</b> danh sách câu hỏi ở đây là {@link QuestionResponse} (bản an toàn),
 * không chứa isCorrect / correctText / explanation.</p>
 */
@Schema(name = "QuizDetailResponse", description = "Chi tiết đề kiểm tra để làm bài (đã ẩn đáp án)")
public record QuizDetailResponse(

        @Schema(description = "ID đề thi") Long id,

        @Schema(description = "Slug trên URL") String slug,

        @Schema(description = "Tiêu đề đề thi") String title,

        @Schema(description = "Mô tả") String description,

        @Schema(description = "ID khoá học, có thể null") Long courseId,

        @Schema(description = "ID bài học, có thể null") Long lessonId,

        @Schema(description = "Cấp độ HSK, có thể null") Integer hskLevel,

        @Schema(description = "Giới hạn thời gian (giây)") Integer timeLimitSeconds,

        @Schema(description = "Ngưỡng phần trăm để đạt") Integer passScore,

        @Schema(description = "Đã xuất bản hay chưa") Boolean published,

        @Schema(description = "Số câu hỏi") Integer questionCount,

        @Schema(description = "Tổng điểm tối đa") Integer maxScore,

        @Schema(description = "Danh sách câu hỏi (không kèm đáp án)")
        List<QuestionResponse> questions,

        @Schema(description = "Thời điểm tạo") Instant createdAt,

        @Schema(description = "Thời điểm cập nhật gần nhất") Instant updatedAt
) {
}
