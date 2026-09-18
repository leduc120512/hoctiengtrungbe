package com.example.bewebtiengtrung.module.course.dto;

/** Thông tin rút gọn của bài học (không kèm nội dung) dùng trong chi tiết khóa học. */
public record LessonSummaryResponse(
        Long id,
        String slug,
        String title,
        String summary,
        Integer sortOrder,
        Integer estimatedMinutes,
        Boolean published
) {
}
