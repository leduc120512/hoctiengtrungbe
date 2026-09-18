package com.example.bewebtiengtrung.module.srs.entity;

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
 * Nhật ký từng lần ôn tập – dùng để tính "đã ôn hôm nay" và chuỗi ngày học liên tiếp.
 *
 * <p>Bảng {@code review_logs} chỉ có cột {@code reviewed_at}, không có
 * {@code created_at}/{@code updated_at} nên KHÔNG kế thừa {@code BaseEntity}.</p>
 */
@Entity
@Table(name = "review_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating", nullable = false, length = 20)
    private Rating rating;

    /** Khoảng cách (ngày) TRƯỚC khi áp dụng SM-2. */
    @Builder.Default
    @Column(name = "previous_interval", nullable = false)
    private Integer previousInterval = 0;

    /** Khoảng cách (ngày) SAU khi áp dụng SM-2. */
    @Builder.Default
    @Column(name = "new_interval", nullable = false)
    private Integer newInterval = 0;

    /** Hệ số dễ sau khi cập nhật. */
    @Builder.Default
    @Column(name = "ease_factor_after", nullable = false)
    private Double easeFactorAfter = 2.5d;

    @Column(name = "reviewed_at", nullable = false)
    private Instant reviewedAt;
}
