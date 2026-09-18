package com.example.bewebtiengtrung.module.srs.mapper;

import com.example.bewebtiengtrung.module.srs.dto.ReviewCardResponse;
import com.example.bewebtiengtrung.module.srs.dto.ReviewStateResponse;
import com.example.bewebtiengtrung.module.srs.entity.Flashcard;
import com.example.bewebtiengtrung.module.srs.entity.ReviewState;
import org.springframework.stereotype.Component;

/** Chuyển đổi trạng thái ôn tập sang DTO. */
@Component
public class ReviewMapper {

    public ReviewStateResponse toResponse(ReviewState state) {
        return new ReviewStateResponse(
                state.getId(),
                state.getFlashcard() != null ? state.getFlashcard().getId() : null,
                state.getStatus(),
                state.getEaseFactor(),
                state.getIntervalDays(),
                state.getRepetitions(),
                state.getLapses(),
                state.getDueAt(),
                state.getLastReviewedAt()
        );
    }

    /**
     * Gộp thông tin thẻ và trạng thái để client hiển thị màn hình ôn tập.
     * Yêu cầu {@code flashcard} và {@code flashcard.deck} đã được fetch sẵn (xem
     * {@code ReviewStateRepository#findDue}) để lấy được tên bộ thẻ mà không sinh N+1.
     */
    public ReviewCardResponse toReviewCard(ReviewState state) {
        Flashcard card = state.getFlashcard();
        return new ReviewCardResponse(
                card.getId(),
                card.getDeckId(),
                card.getDeck() != null ? card.getDeck().getName() : null,
                card.getWordId(),
                card.getFront(),
                card.getBack(),
                card.getHint(),
                card.getAudioUrl(),
                card.getImageUrl(),
                state.getStatus(),
                state.getEaseFactor(),
                state.getIntervalDays(),
                state.getRepetitions(),
                state.getDueAt()
        );
    }
}
