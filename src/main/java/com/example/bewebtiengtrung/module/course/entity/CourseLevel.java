package com.example.bewebtiengtrung.module.course.entity;

/**
 * Cấp độ HSK của một khóa học.
 * Ánh xạ cột {@code courses.level} kiểu VARCHAR(20) với {@code @Enumerated(EnumType.STRING)}.
 */
public enum CourseLevel {
    HSK1,
    HSK2,
    HSK3,
    HSK4,
    HSK5,
    HSK6
}
