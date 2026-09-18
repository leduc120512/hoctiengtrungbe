package com.example.bewebtiengtrung.module.course.repository;

import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.CourseLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Truy vấn khóa học. */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    /**
     * Tìm kiếm khóa học có phân trang.
     *
     * @param level              lọc theo cấp độ HSK, null = không lọc
     * @param q                  từ khóa khớp tiêu đề hoặc slug, null = không lọc
     * @param includeUnpublished true (chỉ dành cho admin) thì lấy cả khóa chưa xuất bản
     */
    @Query(value = """
            select c from Course c
            where (:level is null or c.level = :level)
              and (:q is null or lower(c.title) like lower(concat('%', :q, '%'))
                              or lower(c.slug)  like lower(concat('%', :q, '%')))
              and (:includeUnpublished = true or c.published = true)
            """,
            countQuery = """
            select count(c) from Course c
            where (:level is null or c.level = :level)
              and (:q is null or lower(c.title) like lower(concat('%', :q, '%'))
                              or lower(c.slug)  like lower(concat('%', :q, '%')))
              and (:includeUnpublished = true or c.published = true)
            """)
    Page<Course> search(@Param("level") CourseLevel level,
                        @Param("q") String q,
                        @Param("includeUnpublished") boolean includeUnpublished,
                        Pageable pageable);

    /**
     * Đếm số bài học của nhiều khóa học trong MỘT câu truy vấn để tránh N+1
     * khi trả về danh sách khóa học. Mỗi phần tử là mảng {courseId, soLuongBaiHoc}.
     */
    @Query("""
            select l.course.id, count(l.id) from Lesson l
            where l.course.id in :courseIds
              and (:includeUnpublished = true or l.published = true)
            group by l.course.id
            """)
    List<Object[]> countLessonsByCourseIds(@Param("courseIds") List<Long> courseIds,
                                           @Param("includeUnpublished") boolean includeUnpublished);
}
