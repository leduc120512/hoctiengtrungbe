package com.example.bewebtiengtrung.module.srs.mapper;

import com.example.bewebtiengtrung.module.srs.dto.DeckResponse;
import com.example.bewebtiengtrung.module.srs.entity.Deck;
import org.springframework.stereotype.Component;

/** Chuyển đổi giữa entity Deck và DTO. */
@Component
public class DeckMapper {

    /**
     * Map entity sang DTO.
     *
     * <p>Chỉ đọc khoá ngoại {@code ownerId}/{@code topicId} nên không khởi tạo proxy
     * và không phát sinh truy vấn phụ (tránh N+1 khi liệt kê danh sách).</p>
     */
    public DeckResponse toResponse(Deck deck) {
        return new DeckResponse(
                deck.getId(),
                deck.getOwnerId(),
                deck.getTopicId(),
                deck.getSlug(),
                deck.getName(),
                deck.getDescription(),
                deck.getHskLevel(),
                deck.getIsPublic(),
                deck.getIsSystem(),
                deck.getCardCount(),
                deck.getCreatedAt(),
                deck.getUpdatedAt()
        );
    }
}
