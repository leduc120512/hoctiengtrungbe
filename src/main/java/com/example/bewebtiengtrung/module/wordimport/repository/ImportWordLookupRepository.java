package com.example.bewebtiengtrung.module.wordimport.repository;

import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Truy vấn phụ trợ cho module nhập từ vựng — tra từ theo chữ giản thể.
 *
 * <p>Đặt riêng trong module này (thay vì sửa {@code WordRepository} của module vocabulary)
 * để không đụng vào file của module khác. Cột {@code simplified} đã có chỉ mục
 * {@code ix_words_simplified} nên các truy vấn dưới đây đều rẻ.</p>
 */
@Repository
public interface ImportWordLookupRepository extends JpaRepository<Word, Long> {

    /** Mọi từ có cùng chữ giản thể (từ đa âm cho nhiều bản ghi với pinyin khác nhau). */
    List<Word> findBySimplified(String simplified);

    /** Chữ giản thể đã có trong hệ thống hay chưa — dùng cho cờ {@code inSystem} của gợi ý. */
    boolean existsBySimplified(String simplified);
}
