package com.example.bewebtiengtrung.module.quiz.controller;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuizDetailResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuizSummaryResponse;
import com.example.bewebtiengtrung.module.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API tra cứu đề kiểm tra và bắt đầu làm bài.
 *
 * <p><b>BẢO MẬT:</b> {@code GET /api/v1/quizzes/{id}} trả về {@link QuizDetailResponse},
 * là bản đã lược bỏ isCorrect / correctText / explanation. Không được đổi kiểu trả về của
 * endpoint này sang các DTO "Result" vì sẽ để lộ toàn bộ đáp án trước khi người dùng làm bài.</p>
 */
@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "Đề kiểm tra và bài làm")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "Danh sách đề kiểm tra, lọc theo khoá học / bài học / cấp độ HSK")
    public PageResponse<QuizSummaryResponse> searchQuizzes(
            @Parameter(description = "Lọc theo ID khoá học")
            @RequestParam(required = false) Long courseId,

            @Parameter(description = "Lọc theo ID bài học")
            @RequestParam(required = false) Long lessonId,

            @Parameter(description = "Lọc theo cấp độ HSK (1-6)")
            @RequestParam(required = false) Integer hskLevel,

            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return quizService.searchQuizzes(courseId, lessonId, hskLevel, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết đề kiểm tra để làm bài (không kèm đáp án)")
    public QuizDetailResponse getQuiz(
            @Parameter(description = "ID đề kiểm tra") @PathVariable Long id) {
        return quizService.getQuizForTaking(id);
    }

    @PostMapping("/{id}/attempts")
    @Operation(summary = "Bắt đầu một lần làm bài mới cho đề kiểm tra")
    public ResponseEntity<AttemptResponse> startAttempt(
            @Parameter(description = "ID đề kiểm tra") @PathVariable Long id) {
        AttemptResponse response = quizService.startAttempt(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
