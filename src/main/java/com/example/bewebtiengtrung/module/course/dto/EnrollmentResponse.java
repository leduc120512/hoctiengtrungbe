package com.example.bewebtiengtrung.module.course.dto;

import com.example.bewebtiengtrung.module.course.entity.CourseLevel;

import java.time.Instant;

/** Một khóa học mà người dùng đã ghi danh. */
public record EnrollmentResponse(
        Long id,
        Long courseId,
        String courseSlug,
        String courseTitle,
        CourseLevel level,
        String thumbnailUrl,
        Instant enrolledAt,
        Instant completedAt
) {
}
