package com.example.bewebtiengtrung.module.sentence.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Một câu người dùng dán vào (hoặc AI trả về) để lưu.
 *
 * @param hanzi     câu chữ Hán
 * @param pinyin    pinyin có dấu thanh
 * @param meaningVi nghĩa tiếng Việt
 * @param level     cấp độ 1..3, null ⇒ mặc định 1
 */
public record SentenceInput(
        @NotBlank @Size(max = 200) String hanzi,
        @NotBlank @Size(max = 400) String pinyin,
        @NotBlank @Size(max = 500) String meaningVi,
        @Min(1) @Max(3) Integer level
) {
}
