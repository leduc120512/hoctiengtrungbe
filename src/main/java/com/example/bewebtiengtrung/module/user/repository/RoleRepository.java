package com.example.bewebtiengtrung.module.user.repository;

import com.example.bewebtiengtrung.module.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Truy vấn vai trò. Tên vai trò luôn ở dạng {@code ROLE_USER} / {@code ROLE_ADMIN}.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /** Tìm vai trò theo tên đầy đủ, ví dụ {@code ROLE_USER}. */
    Optional<Role> findByName(String name);

    /** Kiểm tra vai trò đã tồn tại hay chưa. */
    boolean existsByName(String name);
}
