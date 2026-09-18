package com.example.bewebtiengtrung.module.course.entity;

/**
 * Trạng thái học của người dùng trên một bài học.
 * Ánh xạ cột {@code user_lesson_progress.status} kiểu VARCHAR(20).
 */
public enum ProgressStatus {
    /** Chưa bắt đầu học. */
    NOT_STARTED,
    /** Đang học dở. */
    IN_PROGRESS,
    /** Đã hoàn thành bài học. */
    COMPLETED
}
