package com.example.bewebtiengtrung.module.srs.repository;

import com.example.bewebtiengtrung.module.srs.entity.Deck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Truy vấn bộ thẻ. */
public interface DeckRepository extends JpaRepository<Deck, Long> {

    /**
     * Danh sách deck mà người dùng được phép xem: deck công khai, deck hệ thống
     * hoặc deck do chính người dùng sở hữu.
     *
     * <p>Lọc trực tiếp trên hai cột khoá ngoại chỉ-đọc {@code ownerId}/{@code topicId}
     * nên truy vấn không cần join bảng nào; DTO cũng chỉ đọc id nên không sinh N+1.</p>
     */
    @Query("""
            select d from Deck d
            where (d.isPublic = true or d.isSystem = true or d.ownerId = :userId)
              and (:hskLevel is null or d.hskLevel = :hskLevel)
              and (:topicId is null or d.topicId = :topicId)
            """)
    Page<Deck> findVisible(@Param("userId") Long userId,
                           @Param("hskLevel") Integer hskLevel,
                           @Param("topicId") Long topicId,
                           Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
