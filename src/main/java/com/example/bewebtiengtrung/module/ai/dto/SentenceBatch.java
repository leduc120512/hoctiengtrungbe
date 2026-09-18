package com.example.bewebtiengtrung.module.ai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * Gói kết quả mà Claude phải trả về (structured output có kiểu).
 *
 * @param sentences danh sách câu sinh ra
 */
public record SentenceBatch(
        @JsonPropertyDescription("Danh sách câu") List<GeneratedSentence> sentences
) {
}
