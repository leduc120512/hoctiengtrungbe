package com.example.bewebtiengtrung.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Lớp cha cho mọi entity có hai cột {@code created_at} / {@code updated_at}.
 *
 * <p>Các bảng KHÔNG có hai cột này (roles, word_examples, lesson_grammar, enrollments,
 * questions, question_options, quiz_attempts, quiz_attempt_answers, review_logs)
 * tuyệt đối không được kế thừa lớp này — chúng phải tự khai báo khoá chính.</p>
 *
 * <p>Thời gian dùng {@link Instant} vì cột trong MySQL là DATETIME(6) lưu theo UTC
 * (đã cấu hình {@code hibernate.jdbc.time_zone=UTC}).</p>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /** Khoá chính BIGINT AUTO_INCREMENT. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Thời điểm tạo bản ghi — do Spring Data Auditing tự điền. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Thời điểm cập nhật gần nhất — do Spring Data Auditing tự điền. */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Entity mới (chưa được lưu xuống DB) thì id còn null. */
    public boolean isNew() {
        return this.id == null;
    }

    /**
     * So sánh theo id, có tính đến Hibernate proxy: {@code Hibernate.getClass()} trả về
     * lớp entity thật kể cả khi đối tượng đang là proxy.
     * Hai entity chưa có id chỉ bằng nhau khi cùng một tham chiếu.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BaseEntity)) {
            return false;
        }
        if (!Hibernate.getClass(this).equals(Hibernate.getClass(obj))) {
            return false;
        }
        BaseEntity other = (BaseEntity) obj;
        return this.id != null && this.id.equals(other.getId());
    }

    /**
     * Hash ổn định suốt vòng đời entity (không phụ thuộc vào id sinh sau khi insert)
     * VÀ giống nhau giữa entity thật với proxy của nó — nếu dùng {@code getClass()}
     * thì proxy (lớp con sinh tự động) sẽ cho hash khác, phá vỡ hợp đồng
     * equals/hashCode khi entity nằm trong {@code HashSet}/{@code HashMap}.
     */
    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
