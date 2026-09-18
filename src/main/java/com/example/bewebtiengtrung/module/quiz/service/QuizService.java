package com.example.bewebtiengtrung.module.quiz.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AdminQuizDetailResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionRequest;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuizDetailResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuizRequest;
import com.example.bewebtiengtrung.module.quiz.dto.QuizSummaryResponse;
import com.example.bewebtiengtrung.module.quiz.dto.SubmitAttemptRequest;
import org.springframework.data.domain.Pageable;

/** Nghiệp vụ đề kiểm tra: tra cứu đề, làm bài, chấm điểm và quản trị nội dung. */
public interface QuizService {

    // ---------------- Phần công khai / người dùng đã đăng nhập ----------------

    /**
     * Danh sách đề kiểm tra có lọc. Người dùng thường chỉ thấy đề đã publish,
     * quản trị viên thấy tất cả.
     */
    PageResponse<QuizSummaryResponse> searchQuizzes(Long courseId, Long lessonId,
                                                    Integer hskLevel, Pageable pageable);

    /**
     * Chi tiết đề kiểm tra để làm bài.
     * BẢO MẬT: kết quả không bao giờ chứa isCorrect / correctText / explanation.
     */
    QuizDetailResponse getQuizForTaking(Long quizId);

    /** Tạo một lần làm bài mới ở trạng thái IN_PROGRESS cho người dùng hiện tại. */
    AttemptResponse startAttempt(Long quizId);

    /** Nộp bài và chấm điểm. Chỉ chủ nhân của lần làm bài IN_PROGRESS mới được gọi. */
    AttemptResultResponse submitAttempt(Long attemptId, SubmitAttemptRequest request);

    /** Lịch sử làm bài của người dùng hiện tại. */
    PageResponse<AttemptResponse> getMyAttempts(Pageable pageable);

    /** Xem lại kết quả một lần làm bài của chính mình. */
    AttemptResultResponse getMyAttemptResult(Long attemptId);

    // ---------------- Phần quản trị ----------------

    /** Chi tiết đề kèm đáp án cho quản trị viên. */
    AdminQuizDetailResponse getQuizForAdmin(Long quizId);

    /** Tạo đề kiểm tra mới. */
    AdminQuizDetailResponse createQuiz(QuizRequest request);

    /** Cập nhật thông tin đề kiểm tra. */
    AdminQuizDetailResponse updateQuiz(Long quizId, QuizRequest request);

    /** Xoá đề kiểm tra cùng toàn bộ câu hỏi của nó. */
    void deleteQuiz(Long quizId);

    /** Thêm câu hỏi vào đề. */
    QuestionResultResponse addQuestion(Long quizId, QuestionRequest request);

    /** Cập nhật câu hỏi (thay thế toàn bộ danh sách phương án). */
    QuestionResultResponse updateQuestion(Long questionId, QuestionRequest request);

    /** Xoá một câu hỏi khỏi đề. */
    void deleteQuestion(Long questionId);
}
