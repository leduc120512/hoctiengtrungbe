package com.example.bewebtiengtrung.module.quiz.controller;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.SubmitAttemptRequest;
import com.example.bewebtiengtrung.module.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API cho các lần làm bài của chính người dùng đang đăng nhập.
 *
 * <p>Quyền sở hữu được kiểm tra trong service bằng {@code SecurityUtils.currentUserId()},
 * truy cập bài của người khác sẽ nhận lỗi 403.</p>
 */
@RestController
@RequestMapping("/api/v1/me/attempts")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Me - Quiz Attempts", description = "Lịch sử và kết quả làm bài của người dùng hiện tại")
public class MeAttemptController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "Danh sách các lần làm bài của tôi")
    public PageResponse<AttemptResponse> getMyAttempts(
            @PageableDefault(size = 20, sort = "startedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return quizService.getMyAttempts(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem lại kết quả chi tiết một lần làm bài của tôi")
    public AttemptResultResponse getMyAttempt(
            @Parameter(description = "ID lần làm bài") @PathVariable Long id) {
        return quizService.getMyAttemptResult(id);
    }

    @PostMapping("/{attemptId}/submit")
    @Operation(summary = "Nộp bài và chấm điểm, kết quả trả về kèm đáp án đúng và giải thích")
    public AttemptResultResponse submitAttempt(
            @Parameter(description = "ID lần làm bài") @PathVariable Long attemptId,
            @Valid @RequestBody SubmitAttemptRequest request) {
        return quizService.submitAttempt(attemptId, request);
    }
}
