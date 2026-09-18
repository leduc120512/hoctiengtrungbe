package com.example.bewebtiengtrung.module.course.dto;

import java.time.Instant;
import java.util.List;

/** Chi tiết bài học: nội dung + ngữ pháp + từ vựng. */
public record LessonDetailResponse(
        Long id,
        Long courseId,
        String courseSlug,
        String courseTitle,
        String slug,
        String title,
        String summary,
        String content,
        String videoUrl,
        String audioUrl,
        Integer sortOrder,
        Integer estimatedMinutes,
        Boolean published,
        Instant createdAt,
        Instant updatedAt,
        List<LessonGrammarResponse> grammarPoints,
        List<LessonWordResponse> words
) {
}
