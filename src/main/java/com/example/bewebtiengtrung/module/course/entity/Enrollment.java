package com.example.bewebtiengtrung.module.course.entity;

import com.example.bewebtiengtrung.module.user.entity.User;
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
 * Bản ghi ghi danh của người dùng vào một khóa học — ánh xạ bảng {@code enrollments}.
 * Bảng không có created_at/updated_at nên không kế thừa BaseEntity; thời điểm ghi danh
 * được lưu ở cột {@code enrolled_at}.
 */
@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = @UniqueConstraint(name = "uk_enrollments_user_course", columnNames = {"user_id", "course_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Tham chiếu chéo module sang entity User của module user. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrolled_at", nullable = false)
    private Instant enrolledAt;

    /** Được set khi người dùng hoàn thành toàn bộ bài học đã xuất bản của khóa. */
    @Column(name = "completed_at")
    private Instant completedAt;
}
