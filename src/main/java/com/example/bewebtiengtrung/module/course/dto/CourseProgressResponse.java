package com.example.bewebtiengtrung.module.course.dto;

/**
 * Tiến độ tổng thể của người dùng trên một khóa học.
 * percent = completedLessons * 100 / totalLessons (làm tròn), bằng 0 khi khóa chưa có bài học.
 */
public record CourseProgressResponse(
        Long courseId,
        String courseSlug,
        String courseTitle,
        long completedLessons,
        long totalLessons,
        int percent
) {
}
