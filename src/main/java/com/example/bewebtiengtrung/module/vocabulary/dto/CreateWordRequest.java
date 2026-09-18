package com.example.bewebtiengtrung.module.vocabulary.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

/**
 * Dữ liệu tạo mới một từ vựng (chỉ quản trị viên được dùng).
 * Cặp (simplified, pinyin) phải là duy nhất trong toàn hệ thống.
 */
public record CreateWordRequest(

        @NotBlank(message = "Chữ giản thể không được để trống")
        @Size(max = 60, message = "Chữ giản thể tối đa 60 ký tự")
        String simplified,

        @Size(max = 60, message = "Chữ phồn thể tối đa 60 ký tự")
        String traditional,

        @NotBlank(message = "Phiên âm pinyin không được để trống")
        @Size(max = 120, message = "Phiên âm pinyin tối đa 120 ký tự")
        String pinyin,

        @Size(max = 120, message = "Pinyin đánh số thanh điệu tối đa 120 ký tự")
        String pinyinNumbered,

        @NotBlank(message = "Nghĩa tiếng Việt không được để trống")
        @Size(max = 500, message = "Nghĩa tiếng Việt tối đa 500 ký tự")
        String meaningVi,

        @Size(max = 500, message = "Nghĩa tiếng Anh tối đa 500 ký tự")
        String meaningEn,

        @Size(max = 30, message = "Từ loại tối đa 30 ký tự")
        String partOfSpeech,

        @NotNull(message = "Cấp độ HSK không được để trống")
        @Min(value = 1, message = "Cấp độ HSK nhỏ nhất là 1")
        @Max(value = 9, message = "Cấp độ HSK lớn nhất là 9")
        Integer hskLevel,

        @Min(value = 1, message = "Số nét phải lớn hơn 0")
        Integer strokeCount,

        @Min(value = 1, message = "Thứ hạng tần suất phải lớn hơn 0")
        Integer frequencyRank,

        @Size(max = 500, message = "Đường dẫn audio tối đa 500 ký tự")
        String audioUrl,

        @Size(max = 500, message = "Đường dẫn ảnh tối đa 500 ký tự")
        String imageUrl,

        @Size(max = 1000, message = "Ghi chú tối đa 1000 ký tự")
        String note,

        /** Danh sách id chủ đề gán cho từ; có thể null hoặc rỗng. */
        Set<Long> topicIds,

        /** Danh sách câu ví dụ tạo kèm; có thể null hoặc rỗng. */
        @Valid List<CreateWordExampleRequest> examples
) {
}
