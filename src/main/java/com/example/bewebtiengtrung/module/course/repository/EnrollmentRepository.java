package com.example.bewebtiengtrung.module.course.repository;

import com.example.bewebtiengtrung.module.course.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Truy vấn bản ghi ghi danh khóa học. */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    /** Nạp kèm khóa học để tránh N+1 khi hiển thị danh sách khóa đã ghi danh. */
    @EntityGraph(attributePaths = {"course"})
    Page<Enrollment> findByUserId(Long userId, Pageable pageable);
}
