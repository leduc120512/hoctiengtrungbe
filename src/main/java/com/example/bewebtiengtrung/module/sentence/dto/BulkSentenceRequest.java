package com.example.bewebtiengtrung.module.sentence.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Yêu cầu thêm nhiều câu một lần (tối đa 300).
 *
 * @param sentences danh sách câu cần lưu
 */
public record BulkSentenceRequest(
        @NotEmpty @Size(max = 300) List<@Valid SentenceInput> sentences
) {
}
