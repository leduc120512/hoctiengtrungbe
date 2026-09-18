package com.example.bewebtiengtrung.module.srs.repository;

import com.example.bewebtiengtrung.module.srs.entity.ReviewState;
import com.example.bewebtiengtrung.module.srs.entity.ReviewStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Truy vấn trạng thái ôn tập SM-2. */
public interface ReviewStateRepository extends JpaRepository<ReviewState, Long> {

    Optional<ReviewState> findByUserIdAndFlashcardId(Long userId, Long flashcardId);

    /** Id các thẻ mà người dùng đã có trạng thái ôn – dùng để đăng ký deck theo kiểu idempotent. */
    @Query("""
            select rs.flashcard.id from ReviewState rs
            where rs.user.id = :userId and rs.flashcard.id in :flashcardIds
            """)
    List<Long> findExistingFlashcardIds(@Param("userId") Long userId,
                                        @Param("flashcardIds") Collection<Long> flashcardIds);

    /**
     * Các thẻ đã đến hạn ôn của người dùng, sắp xếp theo hạn tăng dần.
     *
     * <p>Fetch sẵn flashcard + deck (cần tên bộ thẻ để hiển thị) nên không sinh N+1.
     * Không fetch word vì DTO chỉ cần {@code wordId} lấy từ khoá ngoại chỉ-đọc.
     * Số lượng giới hạn qua {@link Pageable}.</p>
     */
    @Query("""
            select rs from ReviewState rs
            join fetch rs.flashcard f
            join fetch f.deck
            where rs.user.id = :userId
              and rs.dueAt <= :now
              and (:deckId is null or f.deckId = :deckId)
            order by rs.dueAt asc
            """)
    List<ReviewState> findDue(@Param("userId") Long userId,
                              @Param("now") Instant now,
                              @Param("deckId") Long deckId,
                              Pageable pageable);

    long countByUserIdAndStatus(Long userId, ReviewStatus status);

    /** Số thẻ đến hạn trước một mốc thời gian (dùng mốc đầu ngày mai theo UTC). */
    long countByUserIdAndDueAtLessThan(Long userId, Instant before);

    @Modifying(flushAutomatically = true)
    @Query("delete from ReviewState rs where rs.flashcard.id = :flashcardId")
    int deleteByFlashcardId(@Param("flashcardId") Long flashcardId);

    @Modifying(flushAutomatically = true)
    @Query("""
            delete from ReviewState rs
            where rs.flashcard.id in (select f.id from Flashcard f where f.deckId = :deckId)
            """)
    int deleteByDeckId(@Param("deckId") Long deckId);
}
