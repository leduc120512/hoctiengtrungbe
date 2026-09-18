package com.example.bewebtiengtrung.module.userword.dto;

import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Đánh dấu hàng loạt từ với cùng một trạng thái (idempotent —
 * từ đã có trong sổ được giữ nguyên, chỉ tạo mới những từ còn thiếu).
 */
@Schema(description = "Đánh dấu nhiều từ cùng lúc")
public record BulkUserWordRequest(

        @Schema(description = "Danh sách id từ vựng", example = "[1, 2, 3]")
        @NotEmpty(message = "wordIds không được để trống")
        List<Long> wordIds,

        @Schema(description = "Trạng thái gán cho các từ mới thêm", example = "LEARNED")
        @NotNull(message = "status không được để trống")
        UserWordStatus status
) {
}
