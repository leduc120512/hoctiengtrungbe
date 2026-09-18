package com.example.bewebtiengtrung.module.sentence.entity;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Một câu luyện nghe trong kho "Câu của tôi" — ánh xạ bảng {@code user_sentences} (V15).
 *
 * <p>Bảng KHÔNG có cột {@code updated_at} nên entity không kế thừa {@code BaseEntity}:
 * tự khai báo khoá chính và {@code createdAt} được set trong code lúc lưu.</p>
 *
 * <p>{@link #hanziKey} = chữ Hán đã bỏ mọi dấu câu/khoảng trắng (chỉ giữ ký tự CJK),
 * tính bằng {@code SentenceText.key(hanzi)} — là khoá chống trùng theo từng người dùng.</p>
 */
@Entity
@Table(
        name = "user_sentences",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_sentences_user_key",
                columnNames = {"user_id", "hanzi_key"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Chủ sở hữu câu. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Bản sao chỉ-đọc của khoá ngoại {@code user_id} — dùng để kiểm tra quyền sở hữu
     * mà không cần khởi tạo proxy {@link #user}.
     */
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    /** Câu chữ Hán (giữ nguyên dấu câu). */
    @Column(name = "hanzi", nullable = false, length = 200)
    private String hanzi;

    /** Pinyin có dấu thanh, viết hoa chữ đầu câu. */
    @Column(name = "pinyin", nullable = false, length = 400)
    private String pinyin;

    /** Nghĩa tiếng Việt. */
    @Column(name = "meaning_vi", nullable = false, length = 500)
    private String meaningVi;

    /** Cấp độ câu: 1 (3–5 chữ), 2 (5–7 chữ), 3 (7–11 chữ). */
    @Builder.Default
    @Column(name = "level", nullable = false)
    private Integer level = 1;

    /** Nguồn gốc câu. */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private SentenceSource source = SentenceSource.MANUAL;

    /** Khoá chống trùng: chỉ gồm ký tự CJK của {@link #hanzi}. */
    @Column(name = "hanzi_key", nullable = false, length = 200)
    private String hanziKey;

    /** Thời điểm tạo — set trong code (bảng không dùng auditing). */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
