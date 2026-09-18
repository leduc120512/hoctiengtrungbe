package com.example.bewebtiengtrung.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Du lieu dang ky tai khoan moi.
 * Mat khau gioi han 72 byte vi BCrypt cat bo phan vuot qua 72 byte.
 */
@Schema(description = "Thong tin dang ky tai khoan")
public record RegisterRequest(

        @Schema(description = "Email dang nhap", example = "hocvien@example.com")
        @NotBlank(message = "Email khong duoc de trong")
        @Email(message = "Email khong hop le")
        @Size(max = 190, message = "Email toi da 190 ky tu")
        String email,

        @Schema(description = "Ten dang nhap duy nhat", example = "hocvien01")
        @NotBlank(message = "Ten dang nhap khong duoc de trong")
        @Size(min = 3, max = 60, message = "Ten dang nhap tu 3 den 60 ky tu")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
                message = "Ten dang nhap chi gom chu cai, chu so va cac ky tu . _ -")
        String username,

        @Schema(description = "Mat khau (8 - 72 ky tu)", example = "MatKhau@123")
        @NotBlank(message = "Mat khau khong duoc de trong")
        @Size(min = 8, max = 72, message = "Mat khau tu 8 den 72 ky tu")
        String password,

        @Schema(description = "Ten hien thi", example = "Nguyen Van A")
        @NotBlank(message = "Ten hien thi khong duoc de trong")
        @Size(max = 120, message = "Ten hien thi toi da 120 ky tu")
        String displayName
) {
}
