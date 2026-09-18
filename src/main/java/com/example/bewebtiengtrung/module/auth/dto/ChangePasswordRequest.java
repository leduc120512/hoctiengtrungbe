package com.example.bewebtiengtrung.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Du lieu doi mat khau. Gioi han 72 byte vi BCrypt cat phan vuot qua 72 byte. */
@Schema(description = "Thong tin doi mat khau")
public record ChangePasswordRequest(

        @Schema(description = "Mat khau hien tai")
        @NotBlank(message = "Mat khau hien tai khong duoc de trong")
        @Size(min = 8, max = 72, message = "Mat khau tu 8 den 72 ky tu")
        String currentPassword,

        @Schema(description = "Mat khau moi (8 - 72 ky tu)")
        @NotBlank(message = "Mat khau moi khong duoc de trong")
        @Size(min = 8, max = 72, message = "Mat khau moi tu 8 den 72 ky tu")
        String newPassword
) {
}
