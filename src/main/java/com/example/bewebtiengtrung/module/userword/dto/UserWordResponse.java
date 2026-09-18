package com.example.bewebtiengtrung.module.userword.dto;

import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;

import java.time.Instant;

/**
 * Một từ trong sổ từ đã học, kèm thông tin từ vựng gốc để client hiển thị
 * mà không phải gọi thêm API từ điển.
 *
 * @param id        id bản ghi user_words
 * @param wordId    id từ vựng trong bảng words
 * @param learnedAt thời điểm từ được thêm vào sổ
 */
public record UserWordResponse(
        Long id,
        Long wordId,
        String simplified,
        String traditional,
        String pinyin,
        String meaningVi,
        String meaningEn,
        Integer hskLevel,
        String partOfSpeech,
        UserWordStatus status,
        String note,
        Instant learnedAt
) {
}
