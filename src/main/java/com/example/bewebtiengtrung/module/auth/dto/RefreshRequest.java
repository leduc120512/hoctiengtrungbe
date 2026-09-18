package com.example.bewebtiengtrung.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Refresh token goc do client gui len (dung cho /auth/refresh va /auth/logout). */
@Schema(description = "Refresh token goc")
public record RefreshRequest(

        @Schema(description = "Chuoi refresh token nhan duoc khi dang nhap")
        @NotBlank(message = "Refresh token khong duoc de trong")
        @Size(max = 200, message = "Refresh token khong hop le")
        String refreshToken
) {
}
