package com.example.bewebtiengtrung.module.user.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Người dùng của hệ thống — bảng {@code users}.
 *
 * <p>Email là định danh đăng nhập (unique), mật khẩu chỉ lưu ở dạng băm BCrypt
 * trong cột {@code password_hash} VARCHAR(100).</p>
 *
 * <p>Quan hệ tới {@link Role} để {@code EAGER} vì mỗi lần xác thực đều cần danh sách quyền,
 * số vai trò của một người dùng rất nhỏ nên không gây vấn đề hiệu năng.</p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    /** Email đăng nhập, duy nhất toàn hệ thống. */
    @Column(name = "email", nullable = false, length = 190, unique = true)
    private String email;

    /** Tên đăng nhập hiển thị công khai, duy nhất toàn hệ thống. */
    @Column(name = "username", nullable = false, length = 60, unique = true)
    private String username;

    /** Mật khẩu đã băm bằng BCrypt (60 ký tự). */
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    /** Tên hiển thị (tiếng Việt có dấu). */
    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    /** Đường dẫn ảnh đại diện. */
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /** Ngôn ngữ mẹ đẻ, mặc định {@code vi}. */
    @Builder.Default
    @Column(name = "native_language", nullable = false, length = 10)
    private String nativeLanguage = "vi";

    /** Trình độ HSK hiện tại (1..6), mặc định 1. */
    @Builder.Default
    @Column(name = "current_hsk_level", nullable = false)
    private Integer currentHskLevel = 1;

    /** Trạng thái tài khoản, mặc định {@link UserStatus#ACTIVE}. */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    /** Lần đăng nhập gần nhất (UTC). */
    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    /** Các vai trò được gán, ánh xạ qua bảng nối {@code user_roles}. */
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new LinkedHashSet<>();

    /**
     * Trả về tập vai trò, tự khởi tạo nếu còn null
     * (phòng trường hợp entity được tạo bằng constructor rỗng).
     */
    public Set<Role> getRoles() {
        if (this.roles == null) {
            this.roles = new LinkedHashSet<>();
        }
        return this.roles;
    }

    /** Gán thêm một vai trò cho người dùng. */
    public void addRole(Role role) {
        if (role != null) {
            getRoles().add(role);
        }
    }

    /** Gỡ một vai trò khỏi người dùng. */
    public void removeRole(Role role) {
        if (role != null) {
            getRoles().remove(role);
        }
    }

    /** Kiểm tra người dùng có vai trò theo tên ({@code ROLE_ADMIN}...) hay không. */
    public boolean hasRole(String roleName) {
        if (roleName == null) {
            return false;
        }
        return getRoles().stream().anyMatch(role -> roleName.equals(role.getName()));
    }

    /** Tài khoản có đang hoạt động (được phép đăng nhập) hay không. */
    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }

    /** Điền các giá trị mặc định trước khi insert để luôn thoả ràng buộc NOT NULL. */
    @PrePersist
    void applyDefaults() {
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
        if (this.nativeLanguage == null || this.nativeLanguage.isBlank()) {
            this.nativeLanguage = "vi";
        }
        if (this.currentHskLevel == null) {
            this.currentHskLevel = 1;
        }
    }
}
