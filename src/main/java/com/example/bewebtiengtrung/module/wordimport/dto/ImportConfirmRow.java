package com.example.bewebtiengtrung.module.wordimport.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Một dòng đã được người dùng duyệt và gửi lên để thực thi.
 *
 * <p>Với {@code CREATE} cần đủ {@code simplified}, {@code pinyin}, {@code meaningVi}, {@code hskLevel}.
 * Với {@code UPDATE}/{@code LINK} cần {@code existingWordId}; {@code UPDATE} chỉ ghi đè các trường khác null.</p>
 */
public record ImportConfirmRow(

        @NotNull(message = "Hành động không được để trống")
        ImportAction action,

        Long existingWordId,

        @Size(max = 60, message = "Chữ giản thể tối đa 60 ký tự")
        String simplified,

        @Size(max = 60, message = "Chữ phồn thể tối đa 60 ký tự")
        String traditional,

        @Size(max = 120, message = "Phiên âm pinyin tối đa 120 ký tự")
        String pinyin,

        @Size(max = 120, message = "Pinyin đánh số thanh điệu tối đa 120 ký tự")
        String pinyinNumbered,

        @Size(max = 500, message = "Nghĩa tiếng Việt tối đa 500 ký tự")
        String meaningVi,

        @Size(max = 500, message = "Nghĩa tiếng Anh tối đa 500 ký tự")
        String meaningEn,

        @Size(max = 30, message = "Từ loại tối đa 30 ký tự")
        String partOfSpeech,

        @Min(value = 1, message = "Cấp độ HSK nhỏ nhất là 1")
        @Max(value = 9, message = "Cấp độ HSK lớn nhất là 9")
        Integer hskLevel,

        List<Long> topicIds
) {
}
