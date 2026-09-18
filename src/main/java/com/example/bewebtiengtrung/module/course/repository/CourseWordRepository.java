package com.example.bewebtiengtrung.module.course.repository;

import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository CHỈ ĐỌC dành cho module course khi cần nạp entity Word để gán từ vựng
 * cho bài học (bảng nối lesson_words).
 *
 * Spring Data JPA cho phép khai báo nhiều repository trên cùng một entity, nên interface
 * này không xung đột với repository của module vocabulary; đồng thời giúp module course
 * không phụ thuộc vào chi tiết cài đặt repository của module khác.
 */
@Repository
public interface CourseWordRepository extends JpaRepository<Word, Long> {
}
