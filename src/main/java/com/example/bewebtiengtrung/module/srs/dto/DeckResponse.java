package com.example.bewebtiengtrung.module.srs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** Thông tin một bộ thẻ trả về cho client. */
@Schema(description = "Thông tin bộ thẻ")
public record DeckResponse(
        Long id,
        Long ownerId,
        Long topicId,
        String slug,
        String name,
        String description,
        Integer hskLevel,
        Boolean isPublic,
        Boolean isSystem,
        Integer cardCount,
        Instant createdAt,
        Instant updatedAt
) {
}
