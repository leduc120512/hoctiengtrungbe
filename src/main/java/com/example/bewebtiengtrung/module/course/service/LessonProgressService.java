package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.module.course.dto.CourseProgressResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonProgressRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonProgressResponse;

/** Nghiệp vụ theo dõi tiến độ học của người dùng đang đăng nhập. */
public interface LessonProgressService {

    /**
     * Tạo mới hoặc cập nhật tiến độ của một bài học (upsert theo cặp userId + lessonId).
     * Khi status = COMPLETED thì progressPercent = 100 và completedAt = thời điểm hiện tại.
     */
    LessonProgressResponse upsertLessonProgress(Long lessonId, LessonProgressRequest request);

    /** Tiến độ tổng thể của một khóa học = số bài đã hoàn thành / tổng số bài đã xuất bản. */
    CourseProgressResponse getCourseProgress(Long courseId);
}
