package com.example.bewebtiengtrung.module.course.entity;

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
 * Tiến độ học của một người dùng trên một bài học — ánh xạ bảng {@code user_lesson_progress}.
 * Bảng CÓ created_at/updated_at nên entity kế thừa {@link BaseEntity}.
 */
@Entity
@Table(
        name = "user_lesson_progress",
        uniqueConstraints = @UniqueConstraint(name = "uk_ulp_user_lesson", columnNames = {"user_id", "lesson_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLessonProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProgressStatus status;

    /** Phần trăm hoàn thành, giá trị hợp lệ 0..100. */
    @Column(name = "progress_percent", nullable = false)
    private Integer progressPercent;

    @Column(name = "last_viewed_at")
    private Instant lastViewedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
