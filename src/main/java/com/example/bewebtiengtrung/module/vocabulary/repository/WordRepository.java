package com.example.bewebtiengtrung.module.vocabulary.repository;

import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Truy vấn dữ liệu từ vựng.
 */
@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    /**
     * Tìm kiếm từ vựng với ba bộ lọc tuỳ chọn.
     * Mỗi bộ lọc dùng mẫu ":tham_so IS NULL OR ..." nên tham số null nghĩa là bỏ qua bộ lọc đó.
     *
     * @param q        từ khoá khớp với chữ giản thể / pinyin / pinyin dạng số / nghĩa tiếng Việt.
     *                 Cột {@code pinyin} dùng collation phân biệt dấu (xem migration V13) nên gõ
     *                 "hao" sẽ KHÔNG khớp "hǎo"; vì vậy phải tìm cả trong {@code pinyinNumbered}
     *                 ("hao3") — chuỗi ASCII này giúp tìm kiếm không dấu vẫn ra kết quả.
     * @param hskLevel lọc theo cấp độ HSK
     * @param topicId  lọc theo chủ đề (kiểm tra qua bảng word_topics)
     */
    @Query(
            value = """
                    SELECT w FROM Word w
                    WHERE (:q IS NULL
                           OR LOWER(w.simplified) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.pinyin) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.pinyinNumbered) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.meaningVi) LIKE LOWER(CONCAT('%', :q, '%')))
                      AND (:hskLevel IS NULL OR w.hskLevel = :hskLevel)
                      AND (:topicId IS NULL
                           OR EXISTS (SELECT w2.id FROM Word w2 JOIN w2.topics t2
                                      WHERE w2.id = w.id AND t2.id = :topicId))
                    """,
            countQuery = """
                    SELECT COUNT(w) FROM Word w
                    WHERE (:q IS NULL
                           OR LOWER(w.simplified) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.pinyin) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.pinyinNumbered) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(w.meaningVi) LIKE LOWER(CONCAT('%', :q, '%')))
                      AND (:hskLevel IS NULL OR w.hskLevel = :hskLevel)
                      AND (:topicId IS NULL
                           OR EXISTS (SELECT w2.id FROM Word w2 JOIN w2.topics t2
                                      WHERE w2.id = w.id AND t2.id = :topicId))
                    """
    )
    Page<Word> search(@Param("q") String q,
                      @Param("hskLevel") Integer hskLevel,
                      @Param("topicId") Long topicId,
                      Pageable pageable);

    /**
     * Lấy chi tiết một từ kèm câu ví dụ và chủ đề trong cùng một truy vấn.
     * Dùng @EntityGraph để tránh vấn đề N+1 khi trả về WordDetailResponse.
     */
    @EntityGraph(attributePaths = {"examples", "topics"})
    @Query("SELECT w FROM Word w WHERE w.id = :id")
    Optional<Word> findDetailById(@Param("id") Long id);

    /**
     * Lấy ngẫu nhiên một số từ để luyện tập, có thể lọc theo cấp độ HSK.
     * Dùng native query vì JPQL không hỗ trợ RAND() / LIMIT.
     */
    @Query(
            value = """
                    SELECT * FROM words w
                    WHERE (:hskLevel IS NULL OR w.hsk_level = :hskLevel)
                    ORDER BY RAND()
                    LIMIT :count
                    """,
            nativeQuery = true
    )
    List<Word> findRandomWords(@Param("hskLevel") Integer hskLevel,
                               @Param("count") Integer count);

    /** Kiểm tra trùng cặp (giản thể, pinyin) — ràng buộc uk_words_simplified_pinyin. */
    boolean existsBySimplifiedAndPinyin(String simplified, String pinyin);

    /** Kiểm tra trùng cặp (giản thể, pinyin) với một từ khác — phục vụ cập nhật. */
    boolean existsBySimplifiedAndPinyinAndIdNot(String simplified, String pinyin, Long id);

    /** Lấy tất cả từ đang thuộc một chủ đề — dùng khi xoá chủ đề để gỡ liên kết. */
    @Query("SELECT w FROM Word w JOIN w.topics t WHERE t.id = :topicId")
    List<Word> findAllByTopicId(@Param("topicId") Long topicId);
}
