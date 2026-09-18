package com.example.bewebtiengtrung.module.srs.repository;

import com.example.bewebtiengtrung.module.srs.entity.Flashcard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** Truy vấn thẻ ghi nhớ. */
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    /**
     * Danh sách thẻ của một deck. Không cần join vì DTO chỉ đọc khoá ngoại
     * {@code deckId}/{@code wordId}.
     */
    @Query("select f from Flashcard f where f.deckId = :deckId")
    Page<Flashcard> findPageByDeckId(@Param("deckId") Long deckId, Pageable pageable);

    /**
     * Lấy thẻ kèm deck để kiểm tra quyền sửa/xoá.
     * Deck được fetch join vì {@code DeckAccessPolicy} cần đọc cờ công khai/hệ thống
     * và chủ sở hữu của bộ thẻ.
     */
    @Query("""
            select f from Flashcard f
            join fetch f.deck
            where f.id = :id
            """)
    Optional<Flashcard> findDetailById(@Param("id") Long id);

    /** Chỉ lấy id các thẻ trong deck – dùng cho thao tác đăng ký học hàng loạt. */
    @Query("select f.id from Flashcard f where f.deckId = :deckId")
    List<Long> findIdsByDeckId(@Param("deckId") Long deckId);

    long countByDeckId(Long deckId);
}
