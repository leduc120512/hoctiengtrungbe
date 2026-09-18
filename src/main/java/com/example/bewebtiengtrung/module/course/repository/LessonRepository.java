package com.example.bewebtiengtrung.module.course.repository;

import com.example.bewebtiengtrung.module.course.entity.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Truy vấn bài học. */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Lấy danh sách bài học của một khóa học.
     *
     * @param includeUnpublished true (admin) thì lấy cả bài chưa xuất bản
     */
    @Query("""
            select l from Lesson l
            where l.course.id = :courseId
              and (:includeUnpublished = true or l.published = true)
            order by l.sortOrder asc, l.id asc
            """)
    List<Lesson> findByCourse(@Param("courseId") Long courseId,
                              @Param("includeUnpublished") boolean includeUnpublished);

    /**
     * Nạp bài học kèm ngữ pháp, từ vựng và khóa học trong một lần để tránh N+1.
     *
     * Hai tập con được join chung nên câu SQL sinh ra tích Descartes; cả grammarPoints và
     * words đều phải là Set (xem chú thích trong {@link Lesson}) để Hibernate khử trùng lặp
     * và không ném MultipleBagFetchException.
     */
    @EntityGraph(attributePaths = {"course", "grammarPoints", "words"})
    Optional<Lesson> findWithDetailsById(Long id);

    /** Nạp bài học kèm khóa học (dùng cho luồng cập nhật tiến độ). */
    @EntityGraph(attributePaths = {"course"})
    Optional<Lesson> findWithCourseById(Long id);

    boolean existsByCourseIdAndSlug(Long courseId, String slug);

    boolean existsByCourseIdAndSlugAndIdNot(Long courseId, String slug, Long id);

    long countByCourseIdAndPublishedTrue(Long courseId);
}
