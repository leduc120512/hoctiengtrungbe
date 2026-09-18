package com.example.bewebtiengtrung.module.course.dto;

/** Điểm ngữ pháp trong một bài học. */
public record LessonGrammarResponse(
        Long id,
        Long lessonId,
        String title,
        String structure,
        String explanation,
        String exampleZh,
        String exampleVi,
        Integer sortOrder
) {
}
