package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.course.dto.CourseDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseSummaryResponse;
import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.CourseLevel;
import com.example.bewebtiengtrung.module.course.entity.Lesson;
import com.example.bewebtiengtrung.module.course.mapper.CourseMapper;
import com.example.bewebtiengtrung.module.course.repository.CourseRepository;
import com.example.bewebtiengtrung.module.course.repository.LessonRepository;
import com.example.bewebtiengtrung.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Cài đặt nghiệp vụ đọc khóa học. */
@Service
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseRepository courseRepository,
                             LessonRepository lessonRepository,
                             CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    public PageResponse<CourseSummaryResponse> search(CourseLevel level, String q, Pageable pageable) {
        // Chỉ quản trị viên mới được xem khóa học chưa xuất bản.
        boolean includeUnpublished = SecurityUtils.isAdmin();
        String keyword = (q != null && !q.isBlank()) ? q.trim() : null;

        Page<Course> page = courseRepository.search(level, keyword, includeUnpublished, pageable);
        Map<Long, Long> lessonCounts = countLessons(page.getContent(), includeUnpublished);

        return PageResponse.of(page.map(course ->
                courseMapper.toSummary(course, lessonCounts.getOrDefault(course.getId(), 0L))));
    }

    @Override
    public CourseDetailResponse getBySlug(String slug) {
        boolean includeUnpublished = SecurityUtils.isAdmin();
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học với slug: " + slug));
        if (!includeUnpublished && !Boolean.TRUE.equals(course.getPublished())) {
            // Không tiết lộ sự tồn tại của khóa học chưa xuất bản.
            throw new NotFoundException("Không tìm thấy khóa học với slug: " + slug);
        }
        List<Lesson> lessons = lessonRepository.findByCourse(course.getId(), includeUnpublished);
        return courseMapper.toDetail(course, lessons);
    }

    /**
     * Đếm số bài học cho cả trang kết quả bằng một truy vấn gộp thay vì gọi lặp
     * cho từng khóa học (tránh N+1).
     */
    private Map<Long, Long> countLessons(List<Course> courses, boolean includeUnpublished) {
        Map<Long, Long> counts = new HashMap<>();
        if (courses.isEmpty()) {
            return counts;
        }
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        for (Object[] row : courseRepository.countLessonsByCourseIds(courseIds, includeUnpublished)) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        return counts;
    }
}
