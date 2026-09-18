package com.example.bewebtiengtrung.module.userword.dto;

import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu thêm một từ vào sổ hoặc cập nhật trạng thái / ghi chú của từ đã có.
 * Ngữ nghĩa PUT: ghi chú gửi lên sẽ thay thế ghi chú cũ (null = xoá ghi chú).
 */
@Schema(description = "Thêm hoặc cập nhật một từ trong sổ từ đã học")
public record UpsertUserWordRequest(

        @Schema(description = "Mức độ nắm vững: LEARNING, LEARNED, MASTERED", example = "LEARNED")
        @NotNull(message = "status không được để trống")
        UserWordStatus status,

        @Schema(description = "Ghi chú riêng của người học", example = "Nhớ: 学 = học, 习 = tập")
        @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
        String note
) {
}
