package com.example.bewebtiengtrung.module.quiz.service;

import com.example.bewebtiengtrung.module.quiz.entity.AttemptStatus;
import com.example.bewebtiengtrung.module.quiz.entity.QuizAttempt;
import com.example.bewebtiengtrung.module.quiz.repository.QuizAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

/**
 * Đánh dấu một lần làm bài là EXPIRED trong MỘT TRANSACTION RIÊNG.
 *
 * <p>Lý do phải tách thành bean riêng: khi người dùng nộp bài quá hạn, {@code QuizServiceImpl}
 * phải vừa ghi nhận trạng thái EXPIRED vừa ném BadRequestException để từ chối bài nộp.
 * Nếu ghi trong cùng transaction thì exception sẽ khiến transaction rollback và trạng thái
 * EXPIRED bị mất. Dùng {@code Propagation.REQUIRES_NEW} ở một bean khác để lần ghi này
 * commit độc lập (self-invocation trong cùng bean sẽ không đi qua proxy nên không có tác dụng).</p>
 */
@Service
@RequiredArgsConstructor
public class AttemptExpiryService {

    private final QuizAttemptRepository quizAttemptRepository;

    /**
     * Chuyển lần làm bài sang trạng thái EXPIRED và commit ngay lập tức.
     * Bỏ qua nếu bản ghi không còn ở trạng thái IN_PROGRESS.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markExpired(Long attemptId, Instant now) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId).orElse(null);
        if (attempt == null || attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            return;
        }
        attempt.setStatus(AttemptStatus.EXPIRED);
        attempt.setSubmittedAt(now);
        if (attempt.getStartedAt() != null) {
            attempt.setDurationSeconds((int) Duration.between(attempt.getStartedAt(), now).getSeconds());
        }
        quizAttemptRepository.save(attempt);
    }
}
