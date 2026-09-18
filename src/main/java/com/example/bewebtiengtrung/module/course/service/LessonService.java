package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.module.course.dto.LessonDetailResponse;

/** Nghiệp vụ đọc bài học cho người dùng cuối. */
public interface LessonService {

    /**
     * Chi tiết bài học gồm nội dung, điểm ngữ pháp và từ vựng.
     * Bài học chưa xuất bản (hoặc thuộc khóa chưa xuất bản) chỉ quản trị viên xem được.
     */
    LessonDetailResponse getById(Long lessonId);
}
