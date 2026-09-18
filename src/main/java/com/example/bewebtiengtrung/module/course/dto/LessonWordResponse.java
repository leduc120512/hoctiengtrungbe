package com.example.bewebtiengtrung.module.course.dto;

/**
 * Từ vựng gắn với bài học.
 * Module course tự khai báo DTO này để không phụ thuộc vào DTO của module vocabulary.
 */
public record LessonWordResponse(
        Long id,
        String simplified,
        String traditional,
        String pinyin,
        String meaningVi,
        Integer hskLevel,
        String audioUrl
) {
}
