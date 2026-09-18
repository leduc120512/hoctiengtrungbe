package com.example.bewebtiengtrung.module.wordimport.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Yêu cầu AI điền đủ chữ Hán / pinyin / nghĩa cho nội dung người học dán, rồi duyệt trước như thường.
 *
 * @param text            nội dung thô — mỗi dòng một từ, hoặc một câu yêu cầu như "gợi ý 10 từ về đồ ăn"
 * @param defaultHskLevel cấp HSK gán cho mọi dòng tạo mới và để AI chọn từ đúng trình độ (null = 1)
 */
public record ImportAiRequest(

        @NotBlank(message = "Nội dung không được để trống")
        @Size(max = 4000, message = "Nội dung tối đa 4000 ký tự")
        String text,

        @Min(value = 1, message = "Cấp độ HSK nhỏ nhất là 1")
        @Max(value = 9, message = "Cấp độ HSK lớn nhất là 9")
        Integer defaultHskLevel
) {
}
