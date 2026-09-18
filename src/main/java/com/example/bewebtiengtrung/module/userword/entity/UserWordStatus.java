package com.example.bewebtiengtrung.module.userword.entity;

/**
 * Mức độ nắm vững một từ trong sổ từ đã học của người dùng.
 *
 * <p>Thứ tự khai báo phản ánh tiến trình học: đang học → đã học → đã thuộc lòng.
 * Lưu vào cột {@code user_words.status} dạng chuỗi (VARCHAR(20)).</p>
 */
public enum UserWordStatus {

    /** Mới gặp, đang học. */
    LEARNING,

    /** Đã học xong (giá trị mặc định khi thêm vào sổ). */
    LEARNED,

    /** Đã thuộc lòng, không cần ôn thêm. */
    MASTERED
}
