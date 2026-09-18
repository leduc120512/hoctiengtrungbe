package com.example.bewebtiengtrung.module.sentence.dto;

import java.util.List;

/**
 * Kết quả thêm nhiều câu.
 *
 * @param added      số câu đã lưu mới
 * @param duplicates số câu bị bỏ vì trùng (theo {@code hanzi_key}) với câu đã có hoặc trong cùng đợt
 * @param sentences  các câu vừa lưu
 */
public record BulkSentenceResponse(
        int added,
        int duplicates,
        List<SentenceResponse> sentences
) {
}
