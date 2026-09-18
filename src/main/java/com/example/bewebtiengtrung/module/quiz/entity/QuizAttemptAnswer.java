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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Câu trả lời của một lần làm bài - bảng {@code quiz_attempt_answers}.
 *
 * <p>Bảng không có timestamp chuẩn (chỉ có {@code answered_at}) nên không kế thừa BaseEntity.
 * Ràng buộc UNIQUE (attempt_id, question_id) bảo đảm mỗi câu hỏi chỉ có một bản ghi trả lời
 * trong cùng một lần làm bài.</p>
 */
@Entity
@Table(
        name = "quiz_attempt_answers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_qaa_attempt_question",
                columnNames = {"attempt_id", "question_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** Phương án người dùng đã chọn - NULL với câu tự luận hoặc câu bỏ trống. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    /** Đáp án dạng chữ người dùng nhập - dùng cho FILL_BLANK / TRANSLATION. */
    @Column(name = "text_answer", length = 500)
    private String textAnswer;

    /** Kết quả chấm của câu này. */
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    /** Điểm được cộng cho câu này (0 nếu sai). */
    @Column(name = "points_awarded", nullable = false)
    private Integer pointsAwarded;

    @Column(name = "answered_at", nullable = false)
    private Instant answeredAt;
}
