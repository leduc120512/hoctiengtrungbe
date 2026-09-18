package com.example.bewebtiengtrung.module.user.repository;

import com.example.bewebtiengtrung.module.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Truy vấn người dùng. Email là định danh đăng nhập nên các truy vấn theo email
 * đều nạp sẵn danh sách vai trò bằng {@link EntityGraph} để tránh N+1.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Tìm theo email (phân biệt hoa thường theo collation của MySQL). */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    /** Tìm theo email, bỏ qua hoa thường — dùng cho luồng đăng nhập. */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmailIgnoreCase(String email);

    /** Tìm theo tên đăng nhập. */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    /** Kiểm tra email đã được đăng ký hay chưa. */
    boolean existsByEmailIgnoreCase(String email);

    /** Kiểm tra tên đăng nhập đã tồn tại hay chưa. */
    boolean existsByUsername(String username);

    /** Kiểm tra tên đăng nhập đã tồn tại hay chưa, bỏ qua hoa thường. */
    boolean existsByUsernameIgnoreCase(String username);
}
