package com.example.bewebtiengtrung.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Du lieu cap nhat ho so ca nhan cua nguoi dung dang dang nhap. */
@Schema(description = "Thong tin cap nhat ho so ca nhan")
public record UpdateProfileRequest(

        @Schema(description = "Ten hien thi", example = "Nguyen Van A")
        @NotBlank(message = "Ten hien thi khong duoc de trong")
        @Size(max = 120, message = "Ten hien thi toi da 120 ky tu")
        String displayName,

        @Schema(description = "Duong dan anh dai dien")
        @Size(max = 500, message = "Duong dan anh dai dien toi da 500 ky tu")
        String avatarUrl,

        @Schema(description = "Ma ngon ngu me de", example = "vi")
        @Size(max = 10, message = "Ma ngon ngu toi da 10 ky tu")
        String nativeLanguage,

        @Schema(description = "Trinh do HSK hien tai (1-6)", example = "2")
        @Min(value = 1, message = "Trinh do HSK toi thieu la 1")
        @Max(value = 6, message = "Trinh do HSK toi da la 6")
        Integer currentHskLevel
) {
}
