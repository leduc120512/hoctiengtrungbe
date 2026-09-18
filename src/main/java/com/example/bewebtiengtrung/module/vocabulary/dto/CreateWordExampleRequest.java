package com.example.bewebtiengtrung.module.vocabulary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu tạo mới một câu ví dụ cho từ vựng.
 */
public record CreateWordExampleRequest(

        @NotBlank(message = "Câu ví dụ tiếng Trung không được để trống")
        @Size(max = 500, message = "Câu ví dụ tiếng Trung tối đa 500 ký tự")
        String sentenceZh,

        @Size(max = 800, message = "Phiên âm câu ví dụ tối đa 800 ký tự")
        String sentencePinyin,

        @NotBlank(message = "Bản dịch tiếng Việt không được để trống")
        @Size(max = 800, message = "Bản dịch tiếng Việt tối đa 800 ký tự")
        String sentenceVi,

        @Size(max = 500, message = "Đường dẫn audio tối đa 500 ký tự")
        String audioUrl,

        @Min(value = 0, message = "Thứ tự sắp xếp không được âm")
        Integer sortOrder
) {
}
