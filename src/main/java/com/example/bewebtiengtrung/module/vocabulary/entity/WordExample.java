package com.example.bewebtiengtrung.module.vocabulary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Câu ví dụ minh hoạ cho một từ vựng — ánh xạ bảng {@code word_examples}.
 * Bảng KHÔNG có created_at / updated_at nên KHÔNG kế thừa BaseEntity,
 * phải tự khai báo khoá chính.
 */
@Entity
@Table(name = "word_examples")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordExample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Từ vựng sở hữu câu ví dụ này — luôn LAZY theo quy ước của dự án. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    /** Câu ví dụ viết bằng chữ Hán. */
    @Column(name = "sentence_zh", nullable = false, length = 500)
    private String sentenceZh;

    /** Phiên âm pinyin của câu ví dụ. */
    @Column(name = "sentence_pinyin", length = 800)
    private String sentencePinyin;

    /** Bản dịch tiếng Việt của câu ví dụ. */
    @Column(name = "sentence_vi", nullable = false, length = 800)
    private String sentenceVi;

    /** Đường dẫn file audio đọc câu ví dụ. */
    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    /** Thứ tự hiển thị của câu ví dụ trong một từ. */
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
