package com.example.bewebtiengtrung.module.srs.dto;

import com.example.bewebtiengtrung.module.srs.entity.Rating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Kết quả người dùng tự đánh giá sau khi xem một thẻ. */
@Schema(description = "Dữ liệu chấm điểm một lần ôn thẻ")
public record ReviewRequest(

        @Schema(description = "Id thẻ vừa ôn", example = "12")
        @NotNull(message = "flashcardId không được để trống")
        Long flashcardId,

        @Schema(description = "Mức đánh giá: AGAIN, HARD, GOOD, EASY", example = "GOOD")
        @NotNull(message = "rating không được để trống")
        Rating rating
) {
}
