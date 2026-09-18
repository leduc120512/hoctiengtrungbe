package com.example.bewebtiengtrung.module.srs.dto;

import com.example.bewebtiengtrung.module.srs.entity.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** Một thẻ đang đến hạn ôn, kèm trạng thái SM-2 hiện tại. */
@Schema(description = "Thẻ đến hạn ôn tập")
public record ReviewCardResponse(
        Long flashcardId,
        Long deckId,
        String deckName,
        Long wordId,
        String front,
        String back,
        String hint,
        String audioUrl,
        String imageUrl,
        ReviewStatus status,
        Double easeFactor,
        Integer intervalDays,
        Integer repetitions,
        Instant dueAt
) {
}
