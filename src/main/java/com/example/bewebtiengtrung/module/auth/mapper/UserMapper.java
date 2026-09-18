package com.example.bewebtiengtrung.module.auth.mapper;

import com.example.bewebtiengtrung.module.auth.dto.UserResponse;
import com.example.bewebtiengtrung.module.user.entity.Role;
import com.example.bewebtiengtrung.module.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Chuyen doi entity {@code User} sang DTO. Entity khong bao gio duoc tra thang ra JSON.
 */
@Component
public class UserMapper {

    /** Map user sang DTO cong khai (khong chua password hash). */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getNativeLanguage(),
                user.getCurrentHskLevel(),
                user.getStatus() == null ? null : user.getStatus().name(),
                roleNames(user),
                user.getCreatedAt()
        );
    }

    /**
     * Lay danh sach ten vai tro da sap xep de ket qua on dinh giua cac lan goi.
     */
    public List<String> roleNames(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        Set<Role> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .filter(Objects::nonNull)
                .map(Role::getName)
                .filter(Objects::nonNull)
                .sorted()
                .toList();
    }
}
