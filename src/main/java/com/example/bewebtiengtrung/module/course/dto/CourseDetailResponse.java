package com.example.bewebtiengtrung.module.course.dto;

import com.example.bewebtiengtrung.module.course.entity.CourseLevel;

import java.time.Instant;
import java.util.List;

/** Chi tiết khóa học kèm danh sách bài học (chưa có nội dung bài học). */
public record CourseDetailResponse(
        Long id,
        String slug,
        String title,
        String description,
        CourseLevel level,
        String thumbnailUrl,
        Boolean published,
        Integer sortOrder,
        Integer estimatedMinutes,
        Instant createdAt,
        Instant updatedAt,
        List<LessonSummaryResponse> lessons
) {
}
