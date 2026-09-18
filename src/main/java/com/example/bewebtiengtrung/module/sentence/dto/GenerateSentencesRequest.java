package com.example.bewebtiengtrung.module.sentence.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Yêu cầu AI sinh câu mới từ vốn từ đã học.
 *
 * @param count      số câu muốn có (1..60)
 * @param level      cấp độ 1..3; null = trộn cả 3 cấp
 * @param focusWords chữ Hán muốn lặp nhiều hơn (tuỳ chọn, tối đa 20)
 */
public record GenerateSentencesRequest(
        @NotNull @Min(1) @Max(60) Integer count,
        @Min(1) @Max(3) Integer level,
        @Size(max = 20) List<String> focusWords
) {
}
