package com.example.bewebtiengtrung.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Du lieu dang nhap bang email + mat khau. */
@Schema(description = "Thong tin dang nhap")
public record LoginRequest(

        @Schema(description = "Email hoac ten dang nhap", example = "2222")
        @NotBlank(message = "Tai khoan khong duoc de trong")
        @Size(max = 190, message = "Tai khoan toi da 190 ky tu")
        String email,

        @Schema(description = "Mat khau", example = "MatKhau@123")
        @NotBlank(message = "Mat khau khong duoc de trong")
        @Size(min = 4, max = 72, message = "Mat khau tu 4 den 72 ky tu")
        String password
) {
}
