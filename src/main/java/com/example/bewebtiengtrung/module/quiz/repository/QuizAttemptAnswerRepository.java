package com.example.bewebtiengtrung.module.quiz.repository;

import com.example.bewebtiengtrung.module.quiz.entity.QuizAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Truy vấn câu trả lời của một lần làm bài. */
@Repository
public interface QuizAttemptAnswerRepository extends JpaRepository<QuizAttemptAnswer, Long> {

    /**
     * Nạp kết quả chi tiết: câu trả lời + câu hỏi + toàn bộ phương án + phương án đã chọn,
     * trong một truy vấn duy nhất (chỉ fetch một collection nên an toàn).
     */
    @Query("""
            select distinct a from QuizAttemptAnswer a
            join fetch a.question q
            left join fetch q.options
            left join fetch q.word
            left join fetch a.selectedOption
            where a.attempt.id = :attemptId
            order by q.sortOrder asc, q.id asc
            """)
    List<QuizAttemptAnswer> findByAttemptIdWithQuestion(@Param("attemptId") Long attemptId);

    boolean existsByAttemptId(Long attemptId);
}
