package com.example.bewebtiengtrung.module.srs.mapper;

import com.example.bewebtiengtrung.module.srs.dto.FlashcardRequest;
import com.example.bewebtiengtrung.module.srs.dto.FlashcardResponse;
import com.example.bewebtiengtrung.module.srs.entity.Flashcard;
import org.springframework.stereotype.Component;

/** Chuyển đổi giữa entity Flashcard và DTO. */
@Component
public class FlashcardMapper {

    /** Map entity sang DTO, chỉ đọc khoá ngoại nên không khởi tạo proxy deck/word. */
    public FlashcardResponse toResponse(Flashcard card) {
        return new FlashcardResponse(
                card.getId(),
                card.getDeckId(),
                card.getWordId(),
                card.getFront(),
                card.getBack(),
                card.getHint(),
                card.getAudioUrl(),
                card.getImageUrl(),
                card.getSortOrder(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }

    /**
     * Tạo entity rỗng từ request. Quan hệ deck/word do service gán vì cần
     * kiểm tra quyền và tra cứu tham chiếu.
     */
    public Flashcard toEntity(FlashcardRequest request) {
        Flashcard card = new Flashcard();
        applyRequest(card, request);
        return card;
    }

    /** Áp dụng các trường vô hướng của request lên entity đã có. */
    public void applyRequest(Flashcard card, FlashcardRequest request) {
        card.setFront(request.front());
        card.setBack(request.back());
        card.setHint(request.hint());
        card.setAudioUrl(request.audioUrl());
        card.setImageUrl(request.imageUrl());
        card.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
    }
}
