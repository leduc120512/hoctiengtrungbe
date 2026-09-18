package com.example.bewebtiengtrung.module.course.dto;

import com.example.bewebtiengtrung.module.course.entity.ProgressStatus;

import java.time.Instant;

/** Tiến độ của người dùng trên một bài học. */
public record LessonProgressResponse(
        Long id,
        Long lessonId,
        String lessonTitle,
        Long courseId,
        ProgressStatus status,
        Integer progressPercent,
        Instant lastViewedAt,
        Instant completedAt
) {
}
