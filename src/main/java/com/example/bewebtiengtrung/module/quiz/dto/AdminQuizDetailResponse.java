package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Chi tiết đề kiểm tra dành cho QUẢN TRỊ VIÊN.
 *
 * <p><b>BẢO MẬT:</b> record này chứa {@link QuestionResultResponse} nên LỘ TOÀN BỘ ĐÁP ÁN.
 * Chỉ được trả về từ các endpoint dưới /api/v1/admin/** đã chặn bằng
 * {@code @PreAuthorize("hasRole('ADMIN')")}.</p>
 */
@Schema(name = "AdminQuizDetailResponse", description = "Chi tiết đề kiểm tra kèm đáp án (chỉ dành cho quản trị viên)")
public record AdminQuizDetailResponse(

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

        @Schema(description = "Danh sách câu hỏi kèm đáp án")
        List<QuestionResultResponse> questions,

        @Schema(description = "Thời điểm tạo") Instant createdAt,

        @Schema(description = "Thời điểm cập nhật gần nhất") Instant updatedAt
) {
}
