package com.example.bewebtiengtrung.module.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Dữ liệu nộp bài: danh sách câu trả lời của người dùng. */
@Schema(name = "SubmitAttemptRequest", description = "Nội dung nộp bài")
public record SubmitAttemptRequest(

        @Schema(description = "Danh sách câu trả lời; câu không gửi lên được tính là sai",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Danh sách câu trả lời không được để trống")
        @Valid
        List<AnswerRequest> answers
) {
}
