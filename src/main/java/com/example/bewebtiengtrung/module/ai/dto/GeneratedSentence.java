package com.example.bewebtiengtrung.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Một câu THÔ do AI trả về (chưa qua bộ lọc chữ đã học / chống trùng).
 *
 * <p>Đây cũng là lược đồ JSON mà SDK tự sinh cho structured output, nên mô tả từng
 * trường bằng {@link JsonPropertyDescription} để model hiểu đúng.</p>
 *
 * @param hanzi  câu chữ Hán, giữ dấu câu
 * @param pinyin pinyin có dấu thanh, viết hoa chữ đầu câu
 * @param vi     nghĩa tiếng Việt tự nhiên
 * @param level  cấp độ 1..3
 */
public record GeneratedSentence(
        @JsonPropertyDescription("Câu chữ Hán giản thể, có dấu câu, CHỈ dùng chữ trong danh sách đã học")
        String hanzi,
        @JsonPropertyDescription("Pinyin có dấu thanh, viết hoa chữ đầu câu, mỗi từ cách nhau khoảng trắng, giữ dấu câu")
        String pinyin,
        @JsonPropertyDescription("Nghĩa tiếng Việt tự nhiên của câu")
        String vi,
        @JsonPropertyDescription("Cấp độ câu: 1 (3-5 chữ), 2 (5-7 chữ) hoặc 3 (7-11 chữ)")
        int level
) {
}
