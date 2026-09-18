package com.example.bewebtiengtrung.module.srs.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import com.example.bewebtiengtrung.module.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
 * Trạng thái ôn tập SM-2 của một người dùng trên một thẻ.
 * Mỗi cặp (user, flashcard) chỉ tồn tại đúng một bản ghi.
 *
 * <p>Ánh xạ bảng {@code review_states}.</p>
 */
@Entity
@Table(
        name = "review_states",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_review_states_user_card",
                columnNames = {"user_id", "flashcard_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewState extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReviewStatus status = ReviewStatus.NEW;

    /** Hệ số dễ (EF) của SM-2, không bao giờ nhỏ hơn 1.3. */
    @Builder.Default
    @Column(name = "ease_factor", nullable = false)
    private Double easeFactor = 2.5d;

    /** Khoảng cách (ngày) tới lần ôn kế tiếp. */
    @Builder.Default
    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays = 0;

    /** Số lần trả lời đúng liên tiếp. */
    @Builder.Default
    @Column(name = "repetitions", nullable = false)
    private Integer repetitions = 0;

    /** Số lần quên (đánh giá AGAIN). */
    @Builder.Default
    @Column(name = "lapses", nullable = false)
    private Integer lapses = 0;

    /** Thời điểm thẻ đến hạn ôn lại. */
    @Column(name = "due_at", nullable = false)
    private Instant dueAt;

    @Column(name = "last_reviewed_at")
    private Instant lastReviewedAt;
}
