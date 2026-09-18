package com.example.bewebtiengtrung.module.quiz.repository;

import com.example.bewebtiengtrung.module.quiz.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Truy vấn đề kiểm tra. */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    /**
     * Tìm kiếm cho ADMIN - thấy cả đề chưa publish.
     * Fetch sẵn course/lesson để mapper lấy id mà không sinh truy vấn N+1.
     */
    @Query(value = """
            select q from Quiz q
            left join fetch q.course
            left join fetch q.lesson
            where (:courseId is null or q.course.id = :courseId)
              and (:lessonId is null or q.lesson.id = :lessonId)
              and (:hskLevel is null or q.hskLevel = :hskLevel)
            """,
            countQuery = """
                    select count(q) from Quiz q
                    where (:courseId is null or q.course.id = :courseId)
                      and (:lessonId is null or q.lesson.id = :lessonId)
                      and (:hskLevel is null or q.hskLevel = :hskLevel)
                    """)
    Page<Quiz> searchAll(@Param("courseId") Long courseId,
                         @Param("lessonId") Long lessonId,
                         @Param("hskLevel") Integer hskLevel,
                         Pageable pageable);

    /** Tìm kiếm cho người dùng thường - chỉ đề đã publish. */
    @Query(value = """
            select q from Quiz q
            left join fetch q.course
            left join fetch q.lesson
            where q.published = true
              and (:courseId is null or q.course.id = :courseId)
              and (:lessonId is null or q.lesson.id = :lessonId)
              and (:hskLevel is null or q.hskLevel = :hskLevel)
            """,
            countQuery = """
                    select count(q) from Quiz q
                    where q.published = true
                      and (:courseId is null or q.course.id = :courseId)
                      and (:lessonId is null or q.lesson.id = :lessonId)
                      and (:hskLevel is null or q.hskLevel = :hskLevel)
                    """)
    Page<Quiz> searchPublished(@Param("courseId") Long courseId,
                               @Param("lessonId") Long lessonId,
                               @Param("hskLevel") Integer hskLevel,
                               Pageable pageable);

    /** Lấy đề kèm course/lesson (không kèm câu hỏi - câu hỏi nạp bằng truy vấn riêng). */
    @Query("""
            select q from Quiz q
            left join fetch q.course
            left join fetch q.lesson
            where q.id = :id
            """)
    Optional<Quiz> findDetailById(@Param("id") Long id);

    /** Lấy đề kèm toàn bộ câu hỏi (dùng khi admin sửa/xoá). */
    @Query("""
            select distinct q from Quiz q
            left join fetch q.questions
            where q.id = :id
            """)
    Optional<Quiz> findWithQuestionsById(@Param("id") Long id);

    Optional<Quiz> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
