package com.example.bewebtiengtrung.module.course.repository;

import com.example.bewebtiengtrung.module.course.entity.ProgressStatus;
import com.example.bewebtiengtrung.module.course.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Truy vấn tiến độ học từng bài của người dùng. */
@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {

    Optional<UserLessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    /** Nạp kèm bài học để map sang DTO không sinh thêm truy vấn. */
    @EntityGraph(attributePaths = {"lesson"})
    List<UserLessonProgress> findByUserIdAndLessonCourseId(Long userId, Long courseId);

    /** Đếm số bài học ĐÃ XUẤT BẢN mà người dùng đã hoàn thành trong một khóa học. */
    @Query("""
            select count(p) from UserLessonProgress p
            where p.user.id = :userId
              and p.lesson.course.id = :courseId
              and p.lesson.published = true
              and p.status = :status
            """)
    long countByUserAndCourseAndStatus(@Param("userId") Long userId,
                                       @Param("courseId") Long courseId,
                                       @Param("status") ProgressStatus status);
}
