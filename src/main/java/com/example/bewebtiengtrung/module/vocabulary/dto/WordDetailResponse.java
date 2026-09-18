package com.example.bewebtiengtrung.module.vocabulary.dto;

import java.util.List;

/**
 * Thông tin đầy đủ của một từ vựng, kèm câu ví dụ và các chủ đề liên quan.
 */
public record WordDetailResponse(
        Long id,
        String simplified,
        String traditional,
        String pinyin,
        String pinyinNumbered,
        String meaningVi,
        String meaningEn,
        String partOfSpeech,
        Integer hskLevel,
        Integer strokeCount,
        Integer frequencyRank,
        String audioUrl,
        String imageUrl,
        String note,
        List<WordExampleResponse> examples,
        List<TopicResponse> topics
) {
}
