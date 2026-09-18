package com.example.bewebtiengtrung.module.quiz.dto;

import com.example.bewebtiengtrung.module.quiz.entity.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Dữ liệu tạo / cập nhật một câu hỏi (chỉ dành cho quản trị viên).
 *
 * <p>Câu trắc nghiệm (SINGLE_CHOICE, MULTIPLE_CHOICE, LISTENING, IMAGE_CHOICE) bắt buộc có
 * ít nhất một phương án và ít nhất một phương án đúng.
 * Câu FILL_BLANK / TRANSLATION bắt buộc có {@code correctText}.
 * Các ràng buộc này được kiểm tra trong service vì phụ thuộc vào {@code type}.</p>
 */
@Schema(name = "QuestionRequest", description = "Dữ liệu tạo hoặc cập nhật câu hỏi")
public record QuestionRequest(

        @Schema(description = "Loại câu hỏi", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Loại câu hỏi không được để trống")
        QuestionType type,

        @Schema(description = "Đề bài", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Đề bài không được để trống")
        @Size(max = 1000, message = "Đề bài không được vượt quá 1000 ký tự")
        String prompt,

        @Schema(description = "Phiên âm pinyin của đề bài")
        @Size(max = 1000, message = "Pinyin không được vượt quá 1000 ký tự")
        String promptPinyin,

        @Schema(description = "Bản dịch tiếng Việt của đề bài")
        @Size(max = 1000, message = "Bản dịch không được vượt quá 1000 ký tự")
        String promptVi,

        @Schema(description = "Đường dẫn file audio")
        @Size(max = 500, message = "Đường dẫn audio không được vượt quá 500 ký tự")
        String audioUrl,

        @Schema(description = "Đường dẫn hình ảnh")
        @Size(max = 500, message = "Đường dẫn hình ảnh không được vượt quá 500 ký tự")
        String imageUrl,

        @Schema(description = "Đáp án dạng chữ, bắt buộc với FILL_BLANK và TRANSLATION")
        @Size(max = 500, message = "Đáp án dạng chữ không được vượt quá 500 ký tự")
        String correctText,

        @Schema(description = "Giải thích đáp án")
        String explanation,

        @Schema(description = "Số điểm của câu hỏi, mặc định 1")
        @Min(value = 1, message = "Số điểm nhỏ nhất là 1")
        Integer points,

        @Schema(description = "Thứ tự hiển thị, để trống sẽ tự xếp vào cuối")
        @Min(value = 0, message = "Thứ tự hiển thị không được âm")
        Integer sortOrder,

        @Schema(description = "ID từ vựng liên quan, có thể null")
        Long wordId,

        @Schema(description = "Danh sách phương án trả lời")
        @Valid
        List<QuestionOptionRequest> options
) {
}
