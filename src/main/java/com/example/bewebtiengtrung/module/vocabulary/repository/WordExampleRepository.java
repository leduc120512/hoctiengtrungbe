package com.example.bewebtiengtrung.module.vocabulary.repository;

import com.example.bewebtiengtrung.module.vocabulary.entity.WordExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Truy vấn dữ liệu câu ví dụ của từ vựng.
 */
@Repository
public interface WordExampleRepository extends JpaRepository<WordExample, Long> {

    /** Lấy các câu ví dụ của một từ theo đúng thứ tự hiển thị. */
    List<WordExample> findByWordIdOrderBySortOrderAscIdAsc(Long wordId);

    /** Đếm số câu ví dụ hiện có của một từ — dùng để sinh sortOrder mặc định. */
    long countByWordId(Long wordId);
}
