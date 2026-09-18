package com.example.bewebtiengtrung.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Một từ do AI điền đủ bốn phần từ dòng người học gõ (chỉ chữ Hán, chỉ pinyin, chỉ tiếng Việt…).
 *
 * <p>Cũng là lược đồ JSON cho structured output nên mô tả từng trường bằng
 * {@link JsonPropertyDescription}. Kết quả CHƯA được tin: tầng import vẫn tra CC-CEDICT và
 * đối chiếu hệ thống y như từ người học tự gõ.</p>
 *
 * @param simplified chữ Hán giản thể
 * @param pinyin     pinyin có dấu thanh, chữ thường, âm tiết cách nhau bằng khoảng trắng
 * @param meaningVi  nghĩa tiếng Việt ngắn gọn
 * @param meaningEn  nghĩa tiếng Anh ngắn gọn
 */
public record CompletedWord(
        @JsonPropertyDescription("Chữ Hán giản thể chuẩn từ điển, không dấu câu")
        String simplified,
        @JsonPropertyDescription("Pinyin có dấu thanh, chữ thường, các âm tiết cách nhau bằng khoảng trắng, ví dụ 'xué xí'")
        String pinyin,
        @JsonPropertyDescription("Nghĩa tiếng Việt ngắn gọn, nghĩa thường dùng nhất")
        String meaningVi,
        @JsonPropertyDescription("Nghĩa tiếng Anh ngắn gọn")
        String meaningEn
) {
}
