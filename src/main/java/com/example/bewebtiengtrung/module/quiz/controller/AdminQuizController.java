package com.example.bewebtiengtrung.module.quiz.controller;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AdminQuizDetailResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionRequest;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuizRequest;
import com.example.bewebtiengtrung.module.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API quản trị đề kiểm tra.
 *
 * <p><b>BẢO MẬT:</b> các endpoint ở đây trả về DTO có kèm đáp án
 * ({@link AdminQuizDetailResponse}, {@link QuestionResultResponse}) nên toàn bộ controller
 * bị chặn bằng {@code @PreAuthorize("hasRole('ADMIN')")}.</p>
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Quiz", description = "Quản trị đề kiểm tra và câu hỏi")
public class AdminQuizController {

    private final QuizService quizService;

    @GetMapping("/quizzes/{id}")
    @Operation(summary = "Xem chi tiết đề kiểm tra kèm toàn bộ đáp án")
    public AdminQuizDetailResponse getQuiz(
            @Parameter(description = "ID đề kiểm tra") @PathVariable Long id) {
        return quizService.getQuizForAdmin(id);
    }

    @PostMapping("/quizzes")
    @Operation(summary = "Tạo đề kiểm tra mới")
    public ResponseEntity<AdminQuizDetailResponse> createQuiz(
            @Valid @RequestBody QuizRequest request) {
        AdminQuizDetailResponse response = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/quizzes/{id}")
    @Operation(summary = "Cập nhật thông tin đề kiểm tra")
    public AdminQuizDetailResponse updateQuiz(
            @Parameter(description = "ID đề kiểm tra") @PathVariable Long id,
            @Valid @RequestBody QuizRequest request) {
        return quizService.updateQuiz(id, request);
    }

    @DeleteMapping("/quizzes/{id}")
    @Operation(summary = "Xoá đề kiểm tra cùng toàn bộ câu hỏi")
    public MessageResponse deleteQuiz(
            @Parameter(description = "ID đề kiểm tra") @PathVariable Long id) {
        quizService.deleteQuiz(id);
        return new MessageResponse("Đã xoá đề kiểm tra id=" + id);
    }

    @PostMapping("/quizzes/{id}/questions")
    @Operation(summary = "Thêm câu hỏi vào đề kiểm tra")
    public ResponseEntity<QuestionResultResponse> addQuestion(
            @Parameter(description = "ID đề kiểm tra") @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {
        QuestionResultResponse response = quizService.addQuestion(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/questions/{questionId}")
    @Operation(summary = "Cập nhật câu hỏi và thay thế toàn bộ phương án trả lời")
    public QuestionResultResponse updateQuestion(
            @Parameter(description = "ID câu hỏi") @PathVariable Long questionId,
            @Valid @RequestBody QuestionRequest request) {
        return quizService.updateQuestion(questionId, request);
    }

    @DeleteMapping("/questions/{questionId}")
    @Operation(summary = "Xoá một câu hỏi khỏi đề kiểm tra")
    public MessageResponse deleteQuestion(
            @Parameter(description = "ID câu hỏi") @PathVariable Long questionId) {
        quizService.deleteQuestion(questionId);
        return new MessageResponse("Đã xoá câu hỏi id=" + questionId);
    }
}
