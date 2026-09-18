package com.example.bewebtiengtrung.module.srs.dto;

import com.example.bewebtiengtrung.module.srs.entity.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** Trạng thái SM-2 của một thẻ sau khi ôn. */
@Schema(description = "Trạng thái ôn tập của thẻ sau khi áp dụng SM-2")
public record ReviewStateResponse(
        Long id,
        Long flashcardId,
        ReviewStatus status,
        Double easeFactor,
        Integer intervalDays,
        Integer repetitions,
        Integer lapses,
        Instant dueAt,
        Instant lastReviewedAt
) {
}
