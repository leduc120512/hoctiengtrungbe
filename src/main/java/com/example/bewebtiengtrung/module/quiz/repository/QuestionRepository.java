package com.example.bewebtiengtrung.module.quiz.repository;

import com.example.bewebtiengtrung.module.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Truy vấn câu hỏi. */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Nạp toàn bộ câu hỏi của một đề kèm phương án trong MỘT truy vấn (tránh N+1).
     * Chỉ fetch một collection duy nhất để không gặp MultipleBagFetchException.
     */
    @Query("""
            select distinct q from Question q
            left join fetch q.options
            left join fetch q.word
            where q.quiz.id = :quizId
            order by q.sortOrder asc, q.id asc
            """)
    List<Question> findByQuizIdWithOptions(@Param("quizId") Long quizId);

    /** Nạp một câu hỏi kèm phương án và đề chứa nó (dùng cho admin). */
    @Query("""
            select distinct q from Question q
            join fetch q.quiz
            left join fetch q.options
            left join fetch q.word
            where q.id = :id
            """)
    Optional<Question> findByIdWithOptions(@Param("id") Long id);

    long countByQuizId(Long quizId);

    /** Tổng điểm tối đa của một đề. */
    @Query("select coalesce(sum(q.points), 0) from Question q where q.quiz.id = :quizId")
    long sumPointsByQuizId(@Param("quizId") Long quizId);

    /**
     * Gộp số câu và tổng điểm cho nhiều đề cùng lúc - dùng cho endpoint danh sách
     * để không phải truy vấn từng đề một.
     *
     * @return mỗi phần tử là {quizId, questionCount, maxScore}
     */
    @Query("""
            select q.quiz.id, count(q), coalesce(sum(q.points), 0)
            from Question q
            where q.quiz.id in :quizIds
            group by q.quiz.id
            """)
    List<Object[]> aggregateByQuizIds(@Param("quizIds") Collection<Long> quizIds);

    /** Số thứ tự lớn nhất hiện có trong đề, dùng để tự động xếp câu hỏi mới vào cuối. */
    @Query("select coalesce(max(q.sortOrder), -1) from Question q where q.quiz.id = :quizId")
    int maxSortOrderByQuizId(@Param("quizId") Long quizId);
}
