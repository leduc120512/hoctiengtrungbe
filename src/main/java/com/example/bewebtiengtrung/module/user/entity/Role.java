package com.example.bewebtiengtrung.module.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vai trò của người dùng — bảng {@code roles}.
 *
 * <p>Bảng này KHÔNG có {@code created_at} / {@code updated_at} nên không kế thừa
 * {@code BaseEntity}; khoá chính được khai báo trực tiếp tại đây.</p>
 *
 * <p>Tên vai trò đã ở dạng {@code ROLE_XXX} nên khi ánh xạ sang
 * {@code SimpleGrantedAuthority} tuyệt đối không thêm tiền tố {@code ROLE_} lần nữa.</p>
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    /** Tên vai trò người dùng thông thường. */
    public static final String ROLE_USER = "ROLE_USER";

    /** Tên vai trò quản trị viên. */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tên vai trò dạng {@code ROLE_USER} / {@code ROLE_ADMIN}. */
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    /** Mô tả ngắn bằng tiếng Việt cho màn hình quản trị. */
    @Column(name = "description", length = 255)
    private String description;

    /** Tiện ích tạo nhanh một vai trò theo tên. */
    public static Role of(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isInstance(obj) && !obj.getClass().isInstance(this)) {
            return false;
        }
        Role other = (Role) obj;
        return this.id != null && this.id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Role.class.hashCode();
    }

    @Override
    public String toString() {
        return "Role{id=" + id + ", name='" + name + "'}";
    }
}
