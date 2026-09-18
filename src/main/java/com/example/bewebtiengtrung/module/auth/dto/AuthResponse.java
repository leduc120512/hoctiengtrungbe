package com.example.bewebtiengtrung.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ket qua xac thuc: cap access token + refresh token kem thong tin nguoi dung.
 * Refresh token o day la chuoi GOC - server chi luu SHA-256 cua no.
 */
@Schema(description = "Ket qua xac thuc")
public record AuthResponse(

        @Schema(description = "JWT access token") String accessToken,
        @Schema(description = "Refresh token goc, dung de lay access token moi") String refreshToken,
        @Schema(description = "Loai token", example = "Bearer") String tokenType,
        @Schema(description = "So giay con hieu luc cua access token", example = "3600") long expiresIn,
        @Schema(description = "Thong tin nguoi dung") UserResponse user
) {

    /** Loai token chuan cho header Authorization. */
    public static final String BEARER = "Bearer";

    /** Tao response voi tokenType mac dinh la "Bearer". */
    public static AuthResponse of(String accessToken, String refreshToken, long expiresIn, UserResponse user) {
        return new AuthResponse(accessToken, refreshToken, BEARER, expiresIn, user);
    }
}
