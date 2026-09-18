package com.example.bewebtiengtrung.module.quiz.entity;

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
 * Phương án trả lời - bảng {@code question_options}.
 *
 * <p>Bảng không có timestamp nên không kế thừa BaseEntity.</p>
 *
 * <p><b>CẢNH BÁO BẢO MẬT:</b> {@code isCorrect} (cột {@code is_correct}) chính là đáp án.
 * Không bao giờ được map sang QuestionOptionResponse (DTO công khai).</p>
 */
@Entity
@Table(name = "question_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "pinyin", length = 500)
    private String pinyin;

    /** Cờ đánh dấu đáp án đúng. DỮ LIỆU NHẠY CẢM. */
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
