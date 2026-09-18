package com.example.bewebtiengtrung.module.userword.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Một từ trong sổ từ đã học của người dùng — ánh xạ bảng {@code user_words}.
 *
 * <p>Mỗi cặp (user, word) chỉ tồn tại đúng một bản ghi (khoá {@code uk_user_words_user_word}).
 * Cả hai quan hệ đều LAZY; truy vấn danh sách phải {@code JOIN FETCH uw.word}
 * để tránh N+1 khi dựng DTO (xem {@code UserWordRepository#search}).</p>
 */
@Entity
@Table(
        name = "user_words",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_words_user_word",
                columnNames = {"user_id", "word_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWord extends BaseEntity {

    /** Người dùng sở hữu sổ từ. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Từ vựng gốc trong kho từ (module vocabulary). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    /** Mức độ nắm vững, mặc định {@link UserWordStatus#LEARNED}. */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserWordStatus status = UserWordStatus.LEARNED;

    /** Ghi chú riêng của người học (mẹo nhớ, ví dụ...). */
    @Column(name = "note", length = 500)
    private String note;

    /**
     * Thời điểm từ được thêm vào sổ. Không đổi khi cập nhật trạng thái/ghi chú —
     * dùng để thống kê "số từ học trong 7 ngày gần nhất".
     */
    @Column(name = "learned_at", nullable = false)
    private Instant learnedAt;

    /** Điền giá trị mặc định trước khi insert để luôn thoả ràng buộc NOT NULL. */
    @PrePersist
    void applyDefaults() {
        if (this.status == null) {
            this.status = UserWordStatus.LEARNED;
        }
        if (this.learnedAt == null) {
            this.learnedAt = Instant.now();
        }
    }
}
