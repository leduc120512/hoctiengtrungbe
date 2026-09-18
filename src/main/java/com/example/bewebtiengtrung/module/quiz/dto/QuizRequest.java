package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dữ liệu tạo / cập nhật đề kiểm tra (chỉ dành cho quản trị viên). */
@Schema(name = "QuizRequest", description = "Dữ liệu tạo hoặc cập nhật đề kiểm tra")
public record QuizRequest(

        @Schema(description = "Slug duy nhất trên URL", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Slug không được để trống")
        @Size(max = 150, message = "Slug không được vượt quá 150 ký tự")
        String slug,

        @Schema(description = "Tiêu đề đề thi", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
        String title,

        @Schema(description = "Mô tả đề thi")
        String description,

        @Schema(description = "ID khoá học, có thể null")
        Long courseId,

        @Schema(description = "ID bài học, có thể null")
        Long lessonId,

        @Schema(description = "Cấp độ HSK 1-6, có thể null")
        @Min(value = 1, message = "Cấp độ HSK nhỏ nhất là 1")
        @Max(value = 6, message = "Cấp độ HSK lớn nhất là 6")
        Integer hskLevel,

        @Schema(description = "Giới hạn thời gian tính bằng giây, null nghĩa là không giới hạn")
        @Min(value = 1, message = "Giới hạn thời gian phải lớn hơn 0 giây")
        Integer timeLimitSeconds,

        @Schema(description = "Ngưỡng phần trăm để đạt (0-100)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Ngưỡng đạt không được để trống")
        @Min(value = 0, message = "Ngưỡng đạt nhỏ nhất là 0")
        @Max(value = 100, message = "Ngưỡng đạt lớn nhất là 100")
        Integer passScore,

        @Schema(description = "Xuất bản hay không, mặc định false")
        Boolean published
) {
}
