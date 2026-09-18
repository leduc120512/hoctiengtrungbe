package com.example.bewebtiengtrung.module.quiz.repository;

import com.example.bewebtiengtrung.module.quiz.entity.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Truy vấn các lần làm bài. */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    /**
     * Nạp attempt kèm user và quiz để kiểm tra quyền sở hữu mà không kích hoạt lazy proxy
     * ngoài phạm vi transaction.
     */
    @Query("""
            select a from QuizAttempt a
            join fetch a.user
            join fetch a.quiz
            where a.id = :id
            """)
    Optional<QuizAttempt> findDetailById(@Param("id") Long id);

    /** Lịch sử làm bài của một người dùng, fetch sẵn quiz để hiển thị tiêu đề. */
    @Query(value = """
            select a from QuizAttempt a
            join fetch a.quiz
            where a.user.id = :userId
            """,
            countQuery = "select count(a) from QuizAttempt a where a.user.id = :userId")
    Page<QuizAttempt> findPageByUserId(@Param("userId") Long userId, Pageable pageable);
}
