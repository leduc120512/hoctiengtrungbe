package com.example.bewebtiengtrung.module.course.dto;

import com.example.bewebtiengtrung.module.course.entity.CourseLevel;

/** Thông tin rút gọn của khóa học dùng cho màn hình danh sách. */
public record CourseSummaryResponse(
        Long id,
        String slug,
        String title,
        String description,
        CourseLevel level,
        String thumbnailUrl,
        Boolean published,
        Integer sortOrder,
        Integer estimatedMinutes,
        long lessonCount
) {
}
