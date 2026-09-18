package com.example.bewebtiengtrung.module.srs.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Một thẻ ghi nhớ (flashcard) thuộc về đúng một deck.
 *
 * <p>Ánh xạ bảng {@code flashcards}.</p>
 */
@Entity
@Table(name = "flashcards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flashcard extends BaseEntity {

    /** Deck chứa thẻ này – bắt buộc. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    /**
     * Bản sao chỉ-đọc của khoá ngoại {@code deck_id} để dựng DTO mà không phải
     * khởi tạo proxy của {@link Deck}. Khi gán {@link #deck} nhớ gán kèm trường này.
     */
    @Column(name = "deck_id", nullable = false, insertable = false, updatable = false)
    private Long deckId;

    /** Từ vựng gốc trong kho từ (tuỳ chọn) – cho phép sinh thẻ tự động từ từ điển. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    /**
     * Bản sao chỉ-đọc của khoá ngoại {@code word_id}. Nhờ vậy danh sách thẻ chỉ cần
     * đọc id thay vì join và nạp toàn bộ bản ghi {@link Word}.
     */
    @Column(name = "word_id", insertable = false, updatable = false)
    private Long wordId;

    /** Mặt trước – thường là chữ Hán. */
    @Column(name = "front", nullable = false, length = 500)
    private String front;

    /** Mặt sau – pinyin + nghĩa tiếng Việt. */
    @Column(name = "back", nullable = false, length = 1000)
    private String back;

    @Column(name = "hint", length = 500)
    private String hint;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
