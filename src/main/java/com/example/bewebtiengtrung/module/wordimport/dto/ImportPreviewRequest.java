package com.example.bewebtiengtrung.module.wordimport.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Yêu cầu duyệt trước (preview) một lô từ vựng sắp nhập.
 *
 * @param defaultHskLevel cấp HSK gán cho mọi dòng tạo mới
 * @param defaultTopicIds chủ đề mặc định (tuỳ chọn, FE sẽ gửi lại trong bước confirm)
 * @param rows            tối đa 500 dòng mỗi lần
 */
public record ImportPreviewRequest(

        @NotNull(message = "Cấp độ HSK mặc định không được để trống")
        @Min(value = 1, message = "Cấp độ HSK nhỏ nhất là 1")
        @Max(value = 9, message = "Cấp độ HSK lớn nhất là 9")
        Integer defaultHskLevel,

        List<Long> defaultTopicIds,

        @NotEmpty(message = "Danh sách dòng không được để trống")
        @Size(max = 500, message = "Mỗi lần nhập tối đa 500 dòng")
        List<@Valid ImportRowInput> rows
) {
}
