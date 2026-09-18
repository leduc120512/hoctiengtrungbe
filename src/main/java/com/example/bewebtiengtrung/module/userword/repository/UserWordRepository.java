package com.example.bewebtiengtrung.module.userword.repository;

import com.example.bewebtiengtrung.module.userword.entity.UserWord;
import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Truy vấn sổ từ đã học ({@code user_words}).
 *
 * <p>Mọi truy vấn đều lọc theo {@code user_id} — tầng service không bao giờ để một người
 * dùng nhìn thấy hoặc sửa bản ghi của người khác.</p>
 */
@Repository
public interface UserWordRepository extends JpaRepository<UserWord, Long> {

    /** Số từ theo trạng thái — projection cho truy vấn GROUP BY. */
    interface StatusCount {
        UserWordStatus getStatus();

        long getTotal();
    }

    /** Số từ theo cấp độ HSK của từ gốc — projection cho truy vấn GROUP BY. */
    interface LevelCount {
        Integer getHskLevel();

        long getTotal();
    }

    /** Bản ghi của một người dùng cho một từ (mỗi cặp chỉ có tối đa một dòng). */
    Optional<UserWord> findByUserIdAndWordId(Long userId, Long wordId);

    /**
     * Tìm kiếm trong sổ từ với ba bộ lọc tuỳ chọn (tham số null = bỏ qua bộ lọc).
     * {@code JOIN FETCH uw.word} nạp sẵn từ gốc để mapper không sinh N+1.
     *
     * @param q từ khoá khớp chữ giản thể / pinyin / pinyin dạng số / nghĩa tiếng Việt.
     *          Cột {@code pinyin} phân biệt dấu thanh (migration V13) nên gõ "hao" không
     *          khớp "hǎo"; vì vậy tìm thêm trong {@code pinyinNumbered} ("hao3") để
     *          nhập không dấu vẫn ra kết quả.
     */
    @Query(
            value = """
                    SELECT uw FROM UserWord uw
                    JOIN FETCH uw.word w
                    WHERE uw.user.id = :userId
                      AND (:status IS NULL OR uw.status = :status)
                      AND (:hskLevel IS NULL OR w.hskLevel = :hskLevel)
                      AND (:q IS NULL
                           OR LOWER(w.simplified) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.pinyin) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.pinyinNumbered) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.meaningVi) LIKE LOWER(CONCAT('%', :q, '%')))
                    """,
            countQuery = """
                    SELECT COUNT(uw) FROM UserWord uw
                    JOIN uw.word w
                    WHERE uw.user.id = :userId
                      AND (:status IS NULL OR uw.status = :status)
                      AND (:hskLevel IS NULL OR w.hskLevel = :hskLevel)
                      AND (:q IS NULL
                           OR LOWER(w.simplified) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.pinyin) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.pinyinNumbered) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.meaningVi) LIKE LOWER(CONCAT('%', :q, '%')))
                    """
    )
    Page<UserWord> search(@Param("userId") Long userId,
                          @Param("status") UserWordStatus status,
                          @Param("hskLevel") Integer hskLevel,
                          @Param("q") String q,
                          Pageable pageable);

    /** Tổng số từ trong sổ của một người dùng. */
    long countByUserId(Long userId);

    /** Số từ ở một trạng thái cụ thể. */
    long countByUserIdAndStatus(Long userId, UserWordStatus status);

    /** Số từ có {@code learnedAt} từ mốc {@code since} trở đi (dùng cho "học trong tuần"). */
    long countByUserIdAndLearnedAtGreaterThanEqual(Long userId, Instant since);

    /** Đếm số từ theo từng trạng thái bằng một truy vấn GROUP BY. */
    @Query("""
            SELECT uw.status AS status, COUNT(uw) AS total
            FROM UserWord uw
            WHERE uw.user.id = :userId
            GROUP BY uw.status
            """)
    List<StatusCount> countGroupByStatus(@Param("userId") Long userId);

    /** Đếm số từ theo cấp độ HSK của từ gốc bằng một truy vấn GROUP BY. */
    @Query("""
            SELECT w.hskLevel AS hskLevel, COUNT(uw) AS total
            FROM UserWord uw
            JOIN uw.word w
            WHERE uw.user.id = :userId
            GROUP BY w.hskLevel
            ORDER BY w.hskLevel ASC
            """)
    List<LevelCount> countGroupByHskLevel(@Param("userId") Long userId);

    /** Mọi {@code word_id} có trong sổ của người dùng (không phân biệt trạng thái). */
    @Query("SELECT uw.word.id FROM UserWord uw WHERE uw.user.id = :userId")
    List<Long> findWordIdsByUserId(@Param("userId") Long userId);

    /**
     * Trong một tập id từ cho trước, những id nào đã có trong sổ của người dùng —
     * dùng để đánh dấu hàng loạt theo kiểu idempotent.
     */
    @Query("""
            SELECT uw.word.id FROM UserWord uw
            WHERE uw.user.id = :userId AND uw.word.id IN :wordIds
            """)
    List<Long> findExistingWordIds(@Param("userId") Long userId,
                                   @Param("wordIds") Collection<Long> wordIds);
}
