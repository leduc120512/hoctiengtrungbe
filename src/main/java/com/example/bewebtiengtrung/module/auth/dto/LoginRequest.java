package com.example.bewebtiengtrung.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Du lieu dang nhap bang email + mat khau. */
@Schema(description = "Thong tin dang nhap")
public record LoginRequest(

        @Schema(description = "Email dang nhap", example = "hocvien@example.com")
        @NotBlank(message = "Email khong duoc de trong")
        @Email(message = "Email khong hop le")
        @Size(max = 190, message = "Email toi da 190 ky tu")
        String email,

        @Schema(description = "Mat khau", example = "MatKhau@123")
        @NotBlank(message = "Mat khau khong duoc de trong")
        @Size(min = 8, max = 72, message = "Mat khau tu 8 den 72 ky tu")
        String password
) {
}
