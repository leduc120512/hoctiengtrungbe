package com.example.bewebtiengtrung.module.quiz.entity;

import com.example.bewebtiengtrung.module.user.entity.User;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Một lần làm bài - bảng {@code quiz_attempts}.
 *
 * <p>Bảng chỉ có {@code started_at} / {@code submitted_at}, KHÔNG có created_at/updated_at
 * nên không kế thừa BaseEntity.</p>
 */
@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Người làm bài - dùng để kiểm tra quyền sở hữu khi nộp bài. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AttemptStatus status;

    /** Mốc bắt đầu, dùng để tính hạn nộp khi đề có timeLimitSeconds. */
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    /** Tổng điểm đạt được. */
    @Column(name = "score", nullable = false)
    private Integer score;

    /** Tổng điểm tối đa của đề tại thời điểm làm bài. */
    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    @Column(name = "correct_count", nullable = false)
    private Integer correctCount;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount;

    /** Đạt hay không, tính theo phần trăm so với quiz.passScore. */
    @Column(name = "passed", nullable = false)
    private Boolean passed;

    /** Số giây từ startedAt đến submittedAt. */
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
}
