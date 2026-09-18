package com.example.bewebtiengtrung.module.vocabulary.dto;

/**
 * Thông tin rút gọn của một từ vựng — dùng cho danh sách và luyện tập.
 */
public record WordSummaryResponse(
        Long id,
        String simplified,
        String traditional,
        String pinyin,
        String meaningVi,
        Integer hskLevel,
        String partOfSpeech,
        String audioUrl
) {
}
