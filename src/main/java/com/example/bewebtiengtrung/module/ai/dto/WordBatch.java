package com.example.bewebtiengtrung.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * Gói kết quả AI phải trả về khi điền từ (structured output có kiểu).
 *
 * @param words danh sách từ đã điền đủ, theo đúng thứ tự người học đưa
 */
public record WordBatch(
        @JsonPropertyDescription("Danh sách từ đã điền đủ, theo đúng thứ tự dòng người học đưa") List<CompletedWord> words
) {
}
