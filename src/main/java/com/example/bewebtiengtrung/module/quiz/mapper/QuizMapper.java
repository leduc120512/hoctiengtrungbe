package com.example.bewebtiengtrung.module.quiz.mapper;

import com.example.bewebtiengtrung.module.quiz.dto.AdminQuizDetailResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptAnswerResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionOptionResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionOptionResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuizDetailResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuizSummaryResponse;
import com.example.bewebtiengtrung.module.quiz.entity.Question;
import com.example.bewebtiengtrung.module.quiz.entity.QuestionOption;
import com.example.bewebtiengtrung.module.quiz.entity.Quiz;
import com.example.bewebtiengtrung.module.quiz.entity.QuizAttempt;
import com.example.bewebtiengtrung.module.quiz.entity.QuizAttemptAnswer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chuyển đổi entity của module quiz sang DTO.
 *
 * <p><b>QUY TẮC BẢO MẬT QUAN TRỌNG NHẤT CỦA MODULE NÀY:</b> mapper có hai nhánh song song.
 * Nhánh "an toàn" ({@code toQuestionResponse} / {@code toOptionResponse}) dùng cho người đang
 * làm bài và KHÔNG BAO GIỜ đọc {@code isCorrect}, {@code correctText}, {@code explanation}.
 * Nhánh "đã lộ đáp án" ({@code toQuestionResultResponse} / {@code toOptionResultResponse})
 * chỉ được gọi sau khi người dùng đã nộp bài, hoặc trong các API quản trị.
 * Không được gộp hai nhánh này lại.</p>
 */
@Component
public class QuizMapper {

    // ------------------------------------------------------------------
    // NHÁNH AN TOÀN - dùng khi người dùng đang làm bài
    // ------------------------------------------------------------------

    /**
     * Map phương án trả lời cho người đang làm bài.
     * BẢO MẬT: cố tình KHÔNG đọc {@code option.getIsCorrect()}.
     */
    public QuestionOptionResponse toOptionResponse(QuestionOption option) {
        return new QuestionOptionResponse(
                option.getId(),
                option.getContent(),
                option.getPinyin(),
                option.getSortOrder()
        );
    }

    /**
     * Map câu hỏi cho người đang làm bài.
     * BẢO MẬT: cố tình KHÔNG đọc {@code correctText} và {@code explanation}.
     */
    public QuestionResponse toQuestionResponse(Question question) {
        List<QuestionOptionResponse> options = new ArrayList<>();
        if (question.getOptions() != null) {
            for (QuestionOption option : question.getOptions()) {
                options.add(toOptionResponse(option));
            }
        }
        return new QuestionResponse(
                question.getId(),
                question.getType(),
                question.getPrompt(),
                question.getPromptPinyin(),
                question.getPromptVi(),
                question.getAudioUrl(),
                question.getImageUrl(),
                question.getPoints(),
                question.getSortOrder(),
                wordIdOf(question),
                options
        );
    }

    // ------------------------------------------------------------------
    // NHÁNH ĐÃ LỘ ĐÁP ÁN - chỉ dùng sau khi nộp bài hoặc cho quản trị viên
    // ------------------------------------------------------------------

    /** BẢO MẬT: có lộ cờ đáp án, chỉ dùng sau khi nộp bài hoặc cho ROLE_ADMIN. */
    public QuestionOptionResultResponse toOptionResultResponse(QuestionOption option) {
        return new QuestionOptionResultResponse(
                option.getId(),
                option.getContent(),
                option.getPinyin(),
                Boolean.TRUE.equals(option.getIsCorrect()),
                option.getSortOrder()
        );
    }

    /** BẢO MẬT: có lộ correctText + explanation, chỉ dùng sau khi nộp bài hoặc cho ROLE_ADMIN. */
    public QuestionResultResponse toQuestionResultResponse(Question question) {
        List<QuestionOptionResultResponse> options = new ArrayList<>();
        if (question.getOptions() != null) {
            for (QuestionOption option : question.getOptions()) {
                options.add(toOptionResultResponse(option));
            }
        }
        return new QuestionResultResponse(
                question.getId(),
                question.getType(),
                question.getPrompt(),
                question.getPromptPinyin(),
                question.getPromptVi(),
                question.getAudioUrl(),
                question.getImageUrl(),
                question.getCorrectText(),
                question.getExplanation(),
                question.getPoints(),
                question.getSortOrder(),
                wordIdOf(question),
                options
        );
    }

    // ------------------------------------------------------------------
    // Đề kiểm tra
    // ------------------------------------------------------------------

    /** Tóm tắt đề thi cho danh sách; số câu và tổng điểm được tính sẵn bằng truy vấn gộp. */
    public QuizSummaryResponse toSummary(Quiz quiz, int questionCount, int maxScore) {
        return new QuizSummaryResponse(
                quiz.getId(),
                quiz.getSlug(),
                quiz.getTitle(),
                quiz.getDescription(),
                courseIdOf(quiz),
                lessonIdOf(quiz),
                quiz.getHskLevel(),
                quiz.getTimeLimitSeconds(),
                quiz.getPassScore(),
                Boolean.TRUE.equals(quiz.getPublished()),
                questionCount,
                maxScore,
                quiz.getCreatedAt(),
                quiz.getUpdatedAt()
        );
    }

    /** Chi tiết đề thi để LÀM BÀI - dùng nhánh mapper an toàn. */
    public QuizDetailResponse toDetail(Quiz quiz, List<Question> questions) {
        List<QuestionResponse> items = new ArrayList<>();
        int maxScore = 0;
        for (Question question : questions) {
            items.add(toQuestionResponse(question));
            maxScore += pointsOf(question);
        }
        return new QuizDetailResponse(
                quiz.getId(),
                quiz.getSlug(),
                quiz.getTitle(),
                quiz.getDescription(),
                courseIdOf(quiz),
                lessonIdOf(quiz),
                quiz.getHskLevel(),
                quiz.getTimeLimitSeconds(),
                quiz.getPassScore(),
                Boolean.TRUE.equals(quiz.getPublished()),
                items.size(),
                maxScore,
                items,
                quiz.getCreatedAt(),
                quiz.getUpdatedAt()
        );
    }

    /** Chi tiết đề thi cho QUẢN TRỊ VIÊN - dùng nhánh mapper đã lộ đáp án. */
    public AdminQuizDetailResponse toAdminDetail(Quiz quiz, List<Question> questions) {
        List<QuestionResultResponse> items = new ArrayList<>();
        int maxScore = 0;
        for (Question question : questions) {
            items.add(toQuestionResultResponse(question));
            maxScore += pointsOf(question);
        }
        return new AdminQuizDetailResponse(
                quiz.getId(),
                quiz.getSlug(),
                quiz.getTitle(),
                quiz.getDescription(),
                courseIdOf(quiz),
                lessonIdOf(quiz),
                quiz.getHskLevel(),
                quiz.getTimeLimitSeconds(),
                quiz.getPassScore(),
                Boolean.TRUE.equals(quiz.getPublished()),
                items.size(),
                maxScore,
                items,
                quiz.getCreatedAt(),
                quiz.getUpdatedAt()
        );
    }

    // ------------------------------------------------------------------
    // Lần làm bài
    // ------------------------------------------------------------------

    /** Thông tin một lần làm bài; {@code attempt.getQuiz()} phải đã được nạp sẵn. */
    public AttemptResponse toAttemptResponse(QuizAttempt attempt) {
        Quiz quiz = attempt.getQuiz();
        Integer timeLimit = quiz != null ? quiz.getTimeLimitSeconds() : null;
        Instant expiresAt = null;
        if (timeLimit != null && timeLimit > 0 && attempt.getStartedAt() != null) {
            expiresAt = attempt.getStartedAt().plusSeconds(timeLimit);
        }
        return new AttemptResponse(
                attempt.getId(),
                quiz != null ? quiz.getId() : null,
                quiz != null ? quiz.getTitle() : null,
                attempt.getStatus(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt(),
                expiresAt,
                attempt.getScore(),
                attempt.getMaxScore(),
                attempt.getCorrectCount(),
                attempt.getQuestionCount(),
                Boolean.TRUE.equals(attempt.getPassed()),
                attempt.getDurationSeconds(),
                timeLimit
        );
    }

    /**
     * Kết quả chấm của một câu.
     * BẢO MẬT: nhúng {@link QuestionResultResponse} nên chỉ gọi cho chủ nhân bài làm đã nộp.
     */
    public AttemptAnswerResultResponse toAnswerResultResponse(QuizAttemptAnswer answer) {
        Question question = answer.getQuestion();
        QuestionOption selected = answer.getSelectedOption();
        return new AttemptAnswerResultResponse(
                question != null ? question.getId() : null,
                selected != null ? selected.getId() : null,
                answer.getTextAnswer(),
                Boolean.TRUE.equals(answer.getIsCorrect()),
                answer.getPointsAwarded(),
                answer.getAnsweredAt(),
                question != null ? toQuestionResultResponse(question) : null
        );
    }

    /**
     * Kết quả đầy đủ của một lần làm bài.
     * BẢO MẬT: chỉ gọi sau khi đã xác thực quyền sở hữu của người gọi.
     */
    public AttemptResultResponse toAttemptResultResponse(QuizAttempt attempt,
                                                        List<QuizAttemptAnswer> answers) {
        Quiz quiz = attempt.getQuiz();
        List<AttemptAnswerResultResponse> items = new ArrayList<>();
        if (answers != null) {
            for (QuizAttemptAnswer answer : answers) {
                items.add(toAnswerResultResponse(answer));
            }
        } else {
            items = Collections.emptyList();
        }
        int score = attempt.getScore() != null ? attempt.getScore() : 0;
        int maxScore = attempt.getMaxScore() != null ? attempt.getMaxScore() : 0;
        int scorePercent = maxScore > 0 ? (score * 100 / maxScore) : 0;
        return new AttemptResultResponse(
                attempt.getId(),
                quiz != null ? quiz.getId() : null,
                quiz != null ? quiz.getTitle() : null,
                attempt.getStatus(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt(),
                score,
                maxScore,
                scorePercent,
                quiz != null ? quiz.getPassScore() : null,
                attempt.getCorrectCount(),
                attempt.getQuestionCount(),
                Boolean.TRUE.equals(attempt.getPassed()),
                attempt.getDurationSeconds(),
                items
        );
    }

    // ------------------------------------------------------------------
    // Tiện ích nội bộ
    // ------------------------------------------------------------------

    /** Điểm của câu hỏi, coi null là 0 để phép cộng luôn an toàn. */
    private int pointsOf(Question question) {
        return question.getPoints() != null ? question.getPoints() : 0;
    }

    private Long courseIdOf(Quiz quiz) {
        return quiz.getCourse() != null ? quiz.getCourse().getId() : null;
    }

    private Long lessonIdOf(Quiz quiz) {
        return quiz.getLesson() != null ? quiz.getLesson().getId() : null;
    }

    private Long wordIdOf(Question question) {
        return question.getWord() != null ? question.getWord().getId() : null;
    }
}
