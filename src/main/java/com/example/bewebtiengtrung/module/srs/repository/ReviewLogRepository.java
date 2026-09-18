package com.example.bewebtiengtrung.module.srs.repository;

import com.example.bewebtiengtrung.module.srs.entity.ReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/** Truy vấn nhật ký ôn tập. */
public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {

    /** Số lượt ôn trong khoảng [from, to). */
    @Query("""
            select count(rl) from ReviewLog rl
            where rl.user.id = :userId and rl.reviewedAt >= :from and rl.reviewedAt < :to
            """)
    long countInRange(@Param("userId") Long userId,
                      @Param("from") Instant from,
                      @Param("to") Instant to);

    /**
     * Danh sách ngày (UTC, định dạng yyyy-MM-dd) có ít nhất một lượt ôn, mới nhất trước.
     * Trả về chuỗi để tránh phụ thuộc vào cách driver ánh xạ kiểu DATE; việc đếm chuỗi
     * ngày liên tiếp được thực hiện trong Java.
     */
    @Query(value = """
            SELECT DISTINCT DATE_FORMAT(rl.reviewed_at, '%Y-%m-%d') AS review_date
            FROM review_logs rl
            WHERE rl.user_id = :userId
            ORDER BY review_date DESC
            LIMIT 400
            """, nativeQuery = true)
    List<String> findRecentReviewDates(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true)
    @Query("delete from ReviewLog rl where rl.flashcard.id = :flashcardId")
    int deleteByFlashcardId(@Param("flashcardId") Long flashcardId);

    @Modifying(flushAutomatically = true)
    @Query("""
            delete from ReviewLog rl
            where rl.flashcard.id in (select f.id from Flashcard f where f.deckId = :deckId)
            """)
    int deleteByDeckId(@Param("deckId") Long deckId);
}
