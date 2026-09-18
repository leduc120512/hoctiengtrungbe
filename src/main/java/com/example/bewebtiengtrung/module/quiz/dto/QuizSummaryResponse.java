package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** Tóm tắt đề kiểm tra dùng cho danh sách phân trang. */
@Schema(name = "QuizSummaryResponse", description = "Thông tin tóm tắt của một đề kiểm tra")
public record QuizSummaryResponse(

        @Schema(description = "ID đề thi") Long id,

        @Schema(description = "Slug trên URL") String slug,

        @Schema(description = "Tiêu đề đề thi") String title,

        @Schema(description = "Mô tả ngắn") String description,

        @Schema(description = "ID khoá học, có thể null") Long courseId,

        @Schema(description = "ID bài học, có thể null") Long lessonId,

        @Schema(description = "Cấp độ HSK, có thể null") Integer hskLevel,

        @Schema(description = "Giới hạn thời gian (giây), null nghĩa là không giới hạn")
        Integer timeLimitSeconds,

        @Schema(description = "Ngưỡng phần trăm để đạt") Integer passScore,

        @Schema(description = "Đã xuất bản hay chưa") Boolean published,

        @Schema(description = "Số câu hỏi") Integer questionCount,

        @Schema(description = "Tổng điểm tối đa") Integer maxScore,

        @Schema(description = "Thời điểm tạo") Instant createdAt,

        @Schema(description = "Thời điểm cập nhật gần nhất") Instant updatedAt
) {
}
