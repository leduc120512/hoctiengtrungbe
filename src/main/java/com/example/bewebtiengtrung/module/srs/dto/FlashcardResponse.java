package com.example.bewebtiengtrung.module.srs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** Thông tin một thẻ ghi nhớ. */
@Schema(description = "Thông tin thẻ ghi nhớ")
public record FlashcardResponse(
        Long id,
        Long deckId,
        Long wordId,
        String front,
        String back,
        String hint,
        String audioUrl,
        String imageUrl,
        Integer sortOrder,
        Instant createdAt,
        Instant updatedAt
) {
}
