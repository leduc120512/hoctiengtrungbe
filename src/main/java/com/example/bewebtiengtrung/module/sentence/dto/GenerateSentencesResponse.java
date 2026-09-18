package com.example.bewebtiengtrung.module.sentence.dto;

import java.util.List;

/**
 * Kết quả một lần AI sinh câu.
 *
 * @param requested       số câu người dùng yêu cầu
 * @param generated       số câu hợp lệ đã lưu
 * @param rejected        số ứng viên bị loại vì dùng chữ chưa học
 * @param duplicates      số ứng viên bị loại vì trùng câu đã có / trùng trong đợt
 * @param sentences       các câu vừa lưu
 * @param rejectedSamples tối đa 5 câu bị loại kèm chữ lạ, dạng "他很高兴。 (lạ: 高兴)"
 * @param model           tên model AI đã dùng
 */
public record GenerateSentencesResponse(
        int requested,
        int generated,
        int rejected,
        int duplicates,
        List<SentenceResponse> sentences,
        List<String> rejectedSamples,
        String model
) {
}
