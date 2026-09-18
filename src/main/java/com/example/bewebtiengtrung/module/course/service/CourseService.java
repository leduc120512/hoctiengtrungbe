package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseSummaryResponse;
import com.example.bewebtiengtrung.module.course.entity.CourseLevel;
import org.springframework.data.domain.Pageable;

/** Nghiệp vụ đọc khóa học cho người dùng cuối. */
public interface CourseService {

    /**
     * Tìm kiếm khóa học có phân trang. Người dùng thường chỉ thấy khóa đã xuất bản,
     * quản trị viên thấy tất cả.
     */
    PageResponse<CourseSummaryResponse> search(CourseLevel level, String q, Pageable pageable);

    /** Chi tiết khóa học theo slug, kèm danh sách bài học (chưa gồm nội dung bài học). */
    CourseDetailResponse getBySlug(String slug);
}
