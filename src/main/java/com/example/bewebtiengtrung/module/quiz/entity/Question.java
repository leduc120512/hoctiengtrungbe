package com.example.bewebtiengtrung.module.quiz.entity;

import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Câu hỏi - bảng {@code questions}.
 *
 * <p>Bảng này KHÔNG có cột created_at/updated_at nên không kế thừa BaseEntity,
 * phải khai báo khoá chính tường minh.</p>
 *
 * <p><b>CẢNH BÁO BẢO MẬT:</b> {@code correctText} và {@code explanation} là dữ liệu đáp án.
 * Tuyệt đối không được đưa vào DTO công khai (QuestionResponse) - chúng chỉ được lộ ra
 * sau khi người dùng nộp bài, thông qua QuestionResultResponse.</p>
 */
@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Đề thi chứa câu hỏi này. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private QuestionType type;

    /** Nội dung câu hỏi (thường là tiếng Trung). */
    @Column(name = "prompt", nullable = false, length = 1000)
    private String prompt;

    @Column(name = "prompt_pinyin", length = 1000)
    private String promptPinyin;

    @Column(name = "prompt_vi", length = 1000)
    private String promptVi;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Đáp án dạng chữ cho FILL_BLANK / TRANSLATION. DỮ LIỆU NHẠY CẢM. */
    @Column(name = "correct_text", length = 500)
    private String correctText;

    /** Giải thích đáp án. DỮ LIỆU NHẠY CẢM - chỉ lộ ra sau khi nộp bài. */
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    /** Số điểm của câu hỏi. */
    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    /** Từ vựng liên quan - có thể NULL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    /** Các phương án trả lời - con thực sự của câu hỏi nên cascade ALL + orphanRemoval. */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<QuestionOption> options = new ArrayList<>();

    /** Gắn phương án vào câu hỏi và giữ đồng bộ hai chiều. */
    public void addOption(QuestionOption option) {
        this.options.add(option);
        option.setQuestion(this);
    }

    /** Gỡ phương án khỏi câu hỏi; orphanRemoval sẽ xoá bản ghi tương ứng. */
    public void removeOption(QuestionOption option) {
        this.options.remove(option);
        option.setQuestion(null);
    }
}
