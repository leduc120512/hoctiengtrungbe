package com.example.bewebtiengtrung.module.sentence.dto;

import com.example.bewebtiengtrung.module.sentence.entity.SentenceSource;

import java.time.Instant;

/**
 * Một câu trong kho "Câu của tôi" trả về cho client.
 *
 * @param id        khoá chính
 * @param hanzi     câu chữ Hán (giữ dấu câu)
 * @param pinyin    pinyin có dấu thanh
 * @param meaningVi nghĩa tiếng Việt
 * @param level     cấp độ 1..3
 * @param source    nguồn gốc câu (BUILTIN / MANUAL / AI)
 * @param createdAt thời điểm tạo
 */
public record SentenceResponse(
        Long id,
        String hanzi,
        String pinyin,
        String meaningVi,
        int level,
        SentenceSource source,
        Instant createdAt
) {
}
