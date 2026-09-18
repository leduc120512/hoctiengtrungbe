package com.example.bewebtiengtrung.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Thong tin cong khai cua nguoi dung tra ve cho client.
 * Khong bao gio chua passwordHash hay bat ky du lieu nhay cam nao.
 */
@Schema(description = "Thong tin nguoi dung")
public record UserResponse(

        @Schema(description = "Ma nguoi dung") Long id,
        @Schema(description = "Email dang nhap") String email,
        @Schema(description = "Ten dang nhap") String username,
        @Schema(description = "Ten hien thi") String displayName,
        @Schema(description = "Duong dan anh dai dien") String avatarUrl,
        @Schema(description = "Ma ngon ngu me de") String nativeLanguage,
        @Schema(description = "Trinh do HSK hien tai") Integer currentHskLevel,
        @Schema(description = "Trang thai tai khoan", example = "ACTIVE") String status,
        @Schema(description = "Danh sach vai tro") List<String> roles,
        @Schema(description = "Thoi diem tao tai khoan") Instant createdAt
) {
}
