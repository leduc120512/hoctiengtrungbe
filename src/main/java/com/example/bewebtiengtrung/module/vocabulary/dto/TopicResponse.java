package com.example.bewebtiengtrung.module.vocabulary.dto;

/**
 * Dữ liệu trả về cho một chủ đề từ vựng.
 */
public record TopicResponse(
        Long id,
        String slug,
        String nameVi,
        String nameZh,
        String description,
        String icon,
        Integer sortOrder
) {
}
