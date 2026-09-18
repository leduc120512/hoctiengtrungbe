package com.example.bewebtiengtrung.module.srs.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.srs.dto.DeckRequest;
import com.example.bewebtiengtrung.module.srs.dto.DeckResponse;
import com.example.bewebtiengtrung.module.srs.dto.FlashcardRequest;
import com.example.bewebtiengtrung.module.srs.dto.FlashcardResponse;
import org.springframework.data.domain.Pageable;

/** Nghiệp vụ quản lý bộ thẻ và thẻ ghi nhớ. */
public interface DeckService {

    /** Danh sách deck người dùng hiện tại được xem, có lọc theo cấp HSK và chủ đề. */
    PageResponse<DeckResponse> list(Integer hskLevel, Long topicId, Pageable pageable);

    /** Chi tiết một deck, kiểm tra quyền xem. */
    DeckResponse get(Long deckId);

    /** Tạo deck mới thuộc sở hữu người dùng hiện tại. */
    DeckResponse create(DeckRequest request);

    /** Cập nhật deck, yêu cầu quyền sở hữu hoặc ROLE_ADMIN. */
    DeckResponse update(Long deckId, DeckRequest request);

    /** Xoá deck cùng toàn bộ thẻ, trạng thái ôn tập và nhật ký liên quan. */
    void delete(Long deckId);

    /** Danh sách thẻ trong deck. */
    PageResponse<FlashcardResponse> listCards(Long deckId, Pageable pageable);

    /** Thêm thẻ vào deck và đồng bộ lại {@code cardCount}. */
    FlashcardResponse addCard(Long deckId, FlashcardRequest request);

    /** Cập nhật một thẻ. */
    FlashcardResponse updateCard(Long cardId, FlashcardRequest request);

    /** Xoá một thẻ và đồng bộ lại {@code cardCount}. */
    void deleteCard(Long cardId);
}
