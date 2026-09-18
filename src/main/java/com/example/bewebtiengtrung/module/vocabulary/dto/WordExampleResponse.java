package com.example.bewebtiengtrung.module.vocabulary.dto;

/**
 * Dữ liệu trả về cho một câu ví dụ của từ vựng.
 */
public record WordExampleResponse(
        Long id,
        String sentenceZh,
        String sentencePinyin,
        String sentenceVi,
        String audioUrl,
        Integer sortOrder
) {
}
