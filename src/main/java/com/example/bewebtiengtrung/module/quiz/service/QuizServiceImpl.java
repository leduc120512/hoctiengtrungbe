package com.example.bewebtiengtrung.module.quiz.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.common.exception.BadRequestException;
import com.example.bewebtiengtrung.common.exception.ConflictException;
import com.example.bewebtiengtrung.common.exception.ForbiddenException;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.Lesson;
import com.example.bewebtiengtrung.module.quiz.dto.AdminQuizDetailResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AnswerRequest;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptResponse;
import com.example.bewebtiengtrung.module.quiz.dto.AttemptResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionOptionRequest;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionRequest;
import com.example.bewebtiengtrung.module.quiz.dto.QuestionResultResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuizDetailResponse;
import com.example.bewebtiengtrung.module.quiz.dto.QuizRequest;
import com.example.bewebtiengtrung.module.quiz.dto.QuizSummaryResponse;
import com.example.bewebtiengtrung.module.quiz.dto.SubmitAttemptRequest;
import com.example.bewebtiengtrung.module.quiz.entity.AttemptStatus;
import com.example.bewebtiengtrung.module.quiz.entity.Question;
import com.example.bewebtiengtrung.module.quiz.entity.QuestionOption;
import com.example.bewebtiengtrung.module.quiz.entity.QuestionType;
import com.example.bewebtiengtrung.module.quiz.entity.Quiz;
import com.example.bewebtiengtrung.module.quiz.entity.QuizAttempt;
import com.example.bewebtiengtrung.module.quiz.entity.QuizAttemptAnswer;
import com.example.bewebtiengtrung.module.quiz.mapper.QuizMapper;
import com.example.bewebtiengtrung.module.quiz.repository.QuestionRepository;
import com.example.bewebtiengtrung.module.quiz.repository.QuizAttemptAnswerRepository;
import com.example.bewebtiengtrung.module.quiz.repository.QuizAttemptRepository;
import com.example.bewebtiengtrung.module.quiz.repository.QuizRepository;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.security.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Cài đặt nghiệp vụ module quiz.
 *
 * <p>Toàn bộ dữ liệu trả về đều là DTO; entity không bao giờ ra khỏi lớp service.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptAnswerRepository quizAttemptAnswerRepository;
    private final QuizMapper quizMapper;
    private final AttemptExpiryService attemptExpiryService;

    /**
     * Dùng để lấy tham chiếu tới entity của module khác (Course, Lesson, Word, User)
     * mà không phải phụ thuộc vào repository do module khác sở hữu.
     */
    @PersistenceContext
    private EntityManager entityManager;

    // =================================================================
    // 1. TRA CỨU ĐỀ KIỂM TRA
    // =================================================================

    @Override
    public PageResponse<QuizSummaryResponse> searchQuizzes(Long courseId, Long lessonId,
                                                           Integer hskLevel, Pageable pageable) {
        // Người dùng thường chỉ nhìn thấy đề đã xuất bản.
        Page<Quiz> page = SecurityUtils.isAdmin()
                ? quizRepository.searchAll(courseId, lessonId, hskLevel, pageable)
                : quizRepository.searchPublished(courseId, lessonId, hskLevel, pageable);

        // Gộp số câu hỏi và tổng điểm của tất cả đề trong trang bằng MỘT truy vấn (tránh N+1).
        Map<Long, int[]> stats = loadQuizStats(page.getContent());
        return PageResponse.of(page.map(quiz -> {
            int[] stat = stats.get(quiz.getId());
            int questionCount = stat != null ? stat[0] : 0;
            int maxScore = stat != null ? stat[1] : 0;
            return quizMapper.toSummary(quiz, questionCount, maxScore);
        }));
    }

    @Override
    public QuizDetailResponse getQuizForTaking(Long quizId) {
        Quiz quiz = quizRepository.findDetailById(quizId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đề kiểm tra id=" + quizId));
        ensureVisible(quiz);
        List<Question> questions = questionRepository.findByQuizIdWithOptions(quizId);

        // BẢO MẬT: dùng nhánh mapper an toàn - phản hồi này KHÔNG chứa
        // isCorrect / correctText / explanation, nếu không người làm bài sẽ thấy trước đáp án.
        return quizMapper.toDetail(quiz, questions);
    }

    // =================================================================
    // 2. LÀM BÀI
    // =================================================================

    @Override
    @Transactional
    public AttemptResponse startAttempt(Long quizId) {
        Long userId = SecurityUtils.currentUserId();
        Quiz quiz = quizRepository.findDetailById(quizId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đề kiểm tra id=" + quizId));
        ensureVisible(quiz);

        long questionCount = questionRepository.countByQuizId(quizId);
        if (questionCount == 0) {
            throw new BadRequestException("Đề kiểm tra chưa có câu hỏi nào nên không thể bắt đầu làm bài");
        }
        long maxScore = questionRepository.sumPointsByQuizId(quizId);

        User user = entityManager.find(User.class, userId);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy người dùng id=" + userId);
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .user(user)
                .quiz(quiz)
                .status(AttemptStatus.IN_PROGRESS)
                .startedAt(Instant.now())
                .score(0)
                .maxScore((int) maxScore)
                .correctCount(0)
                .questionCount((int) questionCount)
                .passed(Boolean.FALSE)
                .build();
        quizAttemptRepository.save(attempt);
        return quizMapper.toAttemptResponse(attempt);
    }

    @Override
    @Transactional
    public AttemptResultResponse submitAttempt(Long attemptId, SubmitAttemptRequest request) {
        Long userId = SecurityUtils.currentUserId();

        // (1) Lần làm bài phải tồn tại.
        QuizAttempt attempt = quizAttemptRepository.findDetailById(attemptId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lần làm bài id=" + attemptId));

        // (2) Chỉ chủ nhân mới được nộp bài của mình.
        if (!userId.equals(attempt.getUser().getId())) {
            throw new ForbiddenException("Bạn không có quyền nộp lần làm bài này");
        }

        // (3) Chỉ nộp được bài đang ở trạng thái IN_PROGRESS.
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("Lần làm bài đã kết thúc, trạng thái hiện tại là "
                    + attempt.getStatus());
        }

        // (3b) Chốt chặn chống nộp bài hai lần: nếu đã có bản ghi câu trả lời thì bài này
        // đã được chấm ở một request khác. Không có bước này, hai request nộp song song
        // sẽ cùng ghi và va vào UNIQUE uk_qaa_attempt_question, khiến client nhận HTTP 500
        // thay vì một ProblemDetail đúng chuẩn.
        if (quizAttemptAnswerRepository.existsByAttemptId(attemptId)) {
            throw new ConflictException("Lần làm bài id=" + attemptId + " đã được nộp và chấm điểm trước đó");
        }

        Quiz quiz = attempt.getQuiz();
        Instant now = Instant.now();

        // (4) Quá thời gian cho phép -> đánh dấu EXPIRED (commit ở transaction riêng) rồi từ chối.
        Integer timeLimit = quiz.getTimeLimitSeconds();
        if (timeLimit != null && timeLimit > 0) {
            Instant deadline = attempt.getStartedAt().plusSeconds(timeLimit);
            if (now.isAfter(deadline)) {
                attemptExpiryService.markExpired(attempt.getId(), now);
                throw new BadRequestException("Đã quá thời gian làm bài ("
                        + timeLimit + " giây), lần làm bài bị đánh dấu EXPIRED");
            }
        }

        List<Question> questions = questionRepository.findByQuizIdWithOptions(quiz.getId());
        Map<Long, Question> questionById = new LinkedHashMap<>();
        for (Question question : questions) {
            questionById.put(question.getId(), question);
        }

        // (5) Mọi questionId gửi lên phải thuộc đúng đề thi này và không được trùng nhau.
        Map<Long, AnswerRequest> answerByQuestionId = new HashMap<>();
        List<AnswerRequest> submitted = request.answers() != null
                ? request.answers() : Collections.<AnswerRequest>emptyList();
        Set<Long> seen = new HashSet<>();
        for (AnswerRequest answer : submitted) {
            Long questionId = answer.questionId();
            if (!questionById.containsKey(questionId)) {
                throw new BadRequestException("Câu hỏi id=" + questionId
                        + " không thuộc đề kiểm tra id=" + quiz.getId());
            }
            if (!seen.add(questionId)) {
                throw new BadRequestException("Câu hỏi id=" + questionId + " bị gửi trùng lặp");
            }
            answerByQuestionId.put(questionId, answer);
        }

        // (6) Chấm điểm: duyệt theo danh sách câu hỏi của đề để câu bỏ trống cũng được ghi nhận.
        Instant answeredAt = now;
        int score = 0;
        int maxScore = 0;
        int correctCount = 0;
        List<QuizAttemptAnswer> rows = new ArrayList<>();
        for (Question question : questions) {
            int points = question.getPoints() != null ? question.getPoints() : 0;
            maxScore += points;

            AnswerRequest answer = answerByQuestionId.get(question.getId());
            Long selectedOptionId = answer != null ? answer.selectedOptionId() : null;
            String textAnswer = answer != null ? answer.textAnswer() : null;

            boolean correct = grade(question, selectedOptionId, textAnswer);
            int pointsAwarded = correct ? points : 0;
            if (correct) {
                score += points;
                correctCount++;
            }

            rows.add(QuizAttemptAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    // Chỉ lưu phương án thực sự thuộc câu hỏi này; id lạ được coi như bỏ trống.
                    .selectedOption(findOption(question, selectedOptionId))
                    .textAnswer(textAnswer)
                    .isCorrect(correct)
                    .pointsAwarded(pointsAwarded)
                    .answeredAt(answeredAt)
                    .build());
        }

        int passScore = quiz.getPassScore() != null ? quiz.getPassScore() : 0;
        boolean passed = maxScore > 0 && (score * 100 / maxScore) >= passScore;

        attempt.setScore(score);
        attempt.setMaxScore(maxScore);
        attempt.setCorrectCount(correctCount);
        attempt.setQuestionCount(questions.size());
        attempt.setPassed(passed);
        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(now);
        attempt.setDurationSeconds((int) Duration.between(attempt.getStartedAt(), now).getSeconds());

        quizAttemptRepository.save(attempt);
        quizAttemptAnswerRepository.saveAll(rows);

        // Sau khi nộp bài mới được phép lộ đáp án và giải thích.
        return quizMapper.toAttemptResultResponse(attempt, rows);
    }

    @Override
    public PageResponse<AttemptResponse> getMyAttempts(Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        Page<QuizAttempt> page = quizAttemptRepository.findPageByUserId(userId, pageable);
        return PageResponse.of(page.map(quizMapper::toAttemptResponse));
    }

    @Override
    public AttemptResultResponse getMyAttemptResult(Long attemptId) {
        Long userId = SecurityUtils.currentUserId();
        QuizAttempt attempt = quizAttemptRepository.findDetailById(attemptId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lần làm bài id=" + attemptId));
        if (!userId.equals(attempt.getUser().getId())) {
            throw new ForbiddenException("Bạn không có quyền xem lần làm bài này");
        }
        // Bài chưa nộp thì chưa có bản ghi câu trả lời nào nên cũng chưa lộ đáp án.
        List<QuizAttemptAnswer> answers =
                quizAttemptAnswerRepository.findByAttemptIdWithQuestion(attemptId);
        return quizMapper.toAttemptResultResponse(attempt, answers);
    }

    // =================================================================
    // 3. CHẤM ĐIỂM (hàm thuần tuý - không đọc/ghi DB, không sửa entity)
    // =================================================================

    /**
     * Chấm đúng/sai cho MỘT câu hỏi.
     *
     * <p>Hàm thuần tuý: chỉ đọc dữ liệu đã nạp sẵn của {@code question} và dữ liệu người dùng
     * gửi lên, không truy vấn cơ sở dữ liệu và không thay đổi trạng thái của bất kỳ đối tượng nào.
     * Nhờ vậy có thể kiểm thử độc lập.</p>
     *
     * <ul>
     *   <li>SINGLE_CHOICE / LISTENING / IMAGE_CHOICE: đúng khi phương án được chọn có
     *       {@code isCorrect = true}. Nếu {@code selectedOptionId} là null hoặc không thuộc
     *       câu hỏi này thì coi như sai.</li>
     *   <li>MULTIPLE_CHOICE: HẠN CHẾ HIỆN TẠI - API mới chỉ nhận một {@code selectedOptionId}
     *       cho mỗi câu hỏi nên tạm thời chấm y hệt SINGLE_CHOICE. Khi nào cho phép gửi lên
     *       danh sách nhiều phương án thì phải sửa lại thành so khớp đúng tập phương án đúng.</li>
     *   <li>FILL_BLANK / TRANSLATION: chuẩn hoá cả hai vế (cắt khoảng trắng đầu/cuối, chuyển
     *       chữ thường, gộp khoảng trắng ở giữa) rồi so sánh với {@code question.correctText}.</li>
     * </ul>
     */
    // Để mức package-private (thay vì private) để QuizGradingTest kiểm thử trực tiếp
    // phần logic thuần này mà không phải giả lập toàn bộ repository của submitAttempt.
    boolean grade(Question question, Long selectedOptionId, String textAnswer) {
        QuestionType type = question.getType();
        if (type == null) {
            return false;
        }
        return switch (type) {
            case SINGLE_CHOICE, MULTIPLE_CHOICE, LISTENING, IMAGE_CHOICE ->
                    gradeChoice(question, selectedOptionId);
            case FILL_BLANK, TRANSLATION ->
                    gradeText(question, textAnswer);
        };
    }

    /** Chấm câu trắc nghiệm dựa trên cờ isCorrect của phương án đã chọn. */
    private boolean gradeChoice(Question question, Long selectedOptionId) {
        QuestionOption option = findOption(question, selectedOptionId);
        return option != null && Boolean.TRUE.equals(option.getIsCorrect());
    }

    /** Chấm câu tự luận ngắn bằng cách so sánh chuỗi đã chuẩn hoá. */
    private boolean gradeText(Question question, String textAnswer) {
        String expected = normalizeAnswer(question.getCorrectText());
        String actual = normalizeAnswer(textAnswer);
        return !expected.isEmpty() && expected.equals(actual);
    }

    /**
     * Chuẩn hoá đáp án dạng chữ: cắt khoảng trắng hai đầu, chuyển về chữ thường
     * và gộp mọi chuỗi khoảng trắng ở giữa thành một dấu cách.
     */
    private static String normalizeAnswer(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    /** Tìm phương án theo id trong đúng câu hỏi đang xét; trả về null nếu không thuộc. */
    private QuestionOption findOption(Question question, Long optionId) {
        if (optionId == null || question.getOptions() == null) {
            return null;
        }
        for (QuestionOption option : question.getOptions()) {
            if (optionId.equals(option.getId())) {
                return option;
            }
        }
        return null;
    }

    // =================================================================
    // 4. QUẢN TRỊ NỘI DUNG (ROLE_ADMIN)
    // =================================================================

    @Override
    public AdminQuizDetailResponse getQuizForAdmin(Long quizId) {
        Quiz quiz = quizRepository.findDetailById(quizId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đề kiểm tra id=" + quizId));
        List<Question> questions = questionRepository.findByQuizIdWithOptions(quizId);
        return quizMapper.toAdminDetail(quiz, questions);
    }

    @Override
    @Transactional
    public AdminQuizDetailResponse createQuiz(QuizRequest request) {
        if (quizRepository.existsBySlug(request.slug())) {
            throw new ConflictException("Slug đề kiểm tra đã tồn tại: " + request.slug());
        }
        Quiz quiz = new Quiz();
        quiz.setQuestions(new ArrayList<>());
        applyQuizRequest(quiz, request);
        quizRepository.save(quiz);
        return quizMapper.toAdminDetail(quiz, Collections.emptyList());
    }

    @Override
    @Transactional
    public AdminQuizDetailResponse updateQuiz(Long quizId, QuizRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đề kiểm tra id=" + quizId));
        if (quizRepository.existsBySlugAndIdNot(request.slug(), quizId)) {
            throw new ConflictException("Slug đề kiểm tra đã tồn tại: " + request.slug());
        }
        applyQuizRequest(quiz, request);
        quizRepository.save(quiz);
        List<Question> questions = questionRepository.findByQuizIdWithOptions(quizId);
        return quizMapper.toAdminDetail(quiz, questions);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đề kiểm tra id=" + quizId));
        // cascade ALL + orphanRemoval trên Quiz.questions sẽ xoá kèm câu hỏi và phương án.
        quizRepository.delete(quiz);
    }

    @Override
    @Transactional
    public QuestionResultResponse addQuestion(Long quizId, QuestionRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đề kiểm tra id=" + quizId));
        validateQuestionRequest(request);

        Question question = new Question();
        question.setQuiz(quiz);
        Integer sortOrder = request.sortOrder() != null
                ? request.sortOrder()
                : questionRepository.maxSortOrderByQuizId(quizId) + 1;
        applyQuestionRequest(question, request, sortOrder);
        questionRepository.save(question);
        return quizMapper.toQuestionResultResponse(question);
    }

    @Override
    @Transactional
    public QuestionResultResponse updateQuestion(Long questionId, QuestionRequest request) {
        Question question = questionRepository.findByIdWithOptions(questionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi id=" + questionId));
        validateQuestionRequest(request);
        Integer sortOrder = request.sortOrder() != null ? request.sortOrder() : question.getSortOrder();
        applyQuestionRequest(question, request, sortOrder);
        questionRepository.save(question);
        return quizMapper.toQuestionResultResponse(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi id=" + questionId));
        questionRepository.delete(question);
    }

    // =================================================================
    // 5. TIỆN ÍCH NỘI BỘ
    // =================================================================

    /** Đề chưa xuất bản chỉ hiển thị với quản trị viên; người khác nhận 404 để không lộ sự tồn tại. */
    private void ensureVisible(Quiz quiz) {
        if (!Boolean.TRUE.equals(quiz.getPublished()) && !SecurityUtils.isAdmin()) {
            throw new NotFoundException("Không tìm thấy đề kiểm tra id=" + quiz.getId());
        }
    }

    /** Nạp số câu hỏi và tổng điểm cho cả trang đề thi bằng một truy vấn gộp. */
    private Map<Long, int[]> loadQuizStats(List<Quiz> quizzes) {
        if (quizzes.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> quizIds = new ArrayList<>(quizzes.size());
        for (Quiz quiz : quizzes) {
            quizIds.add(quiz.getId());
        }
        Map<Long, int[]> result = new HashMap<>();
        for (Object[] row : questionRepository.aggregateByQuizIds(quizIds)) {
            Long quizId = ((Number) row[0]).longValue();
            int questionCount = ((Number) row[1]).intValue();
            int maxScore = ((Number) row[2]).intValue();
            result.put(quizId, new int[]{questionCount, maxScore});
        }
        return result;
    }

    /** Gán dữ liệu từ request vào entity đề thi, kèm phân giải khoá học / bài học. */
    private void applyQuizRequest(Quiz quiz, QuizRequest request) {
        quiz.setSlug(request.slug().trim());
        quiz.setTitle(request.title().trim());
        quiz.setDescription(request.description());
        quiz.setHskLevel(request.hskLevel());
        quiz.setTimeLimitSeconds(request.timeLimitSeconds());
        quiz.setPassScore(request.passScore());
        quiz.setPublished(Boolean.TRUE.equals(request.published()));

        if (request.courseId() != null) {
            Course course = entityManager.find(Course.class, request.courseId());
            if (course == null) {
                throw new NotFoundException("Không tìm thấy khoá học id=" + request.courseId());
            }
            quiz.setCourse(course);
        } else {
            quiz.setCourse(null);
        }

        if (request.lessonId() != null) {
            Lesson lesson = entityManager.find(Lesson.class, request.lessonId());
            if (lesson == null) {
                throw new NotFoundException("Không tìm thấy bài học id=" + request.lessonId());
            }
            quiz.setLesson(lesson);
        } else {
            quiz.setLesson(null);
        }
    }

    /**
     * Kiểm tra tính hợp lệ của câu hỏi theo từng loại - phần này không thể diễn đạt bằng
     * annotation validation vì phụ thuộc vào giá trị của trường {@code type}.
     */
    private void validateQuestionRequest(QuestionRequest request) {
        QuestionType type = request.type();
        boolean choiceType = type == QuestionType.SINGLE_CHOICE
                || type == QuestionType.MULTIPLE_CHOICE
                || type == QuestionType.LISTENING
                || type == QuestionType.IMAGE_CHOICE;

        if (choiceType) {
            List<QuestionOptionRequest> options = request.options();
            if (options == null || options.isEmpty()) {
                throw new BadRequestException("Câu hỏi loại " + type + " phải có ít nhất một phương án");
            }
            boolean hasCorrect = false;
            for (QuestionOptionRequest option : options) {
                if (Boolean.TRUE.equals(option.isCorrect())) {
                    hasCorrect = true;
                    break;
                }
            }
            if (!hasCorrect) {
                throw new BadRequestException("Câu hỏi loại " + type + " phải có ít nhất một phương án đúng");
            }
        } else {
            String correctText = request.correctText();
            if (correctText == null || correctText.trim().isEmpty()) {
                throw new BadRequestException("Câu hỏi loại " + type + " bắt buộc phải có correctText");
            }
        }
    }

    /** Gán dữ liệu từ request vào entity câu hỏi và dựng lại toàn bộ danh sách phương án. */
    private void applyQuestionRequest(Question question, QuestionRequest request, Integer sortOrder) {
        question.setType(request.type());
        question.setPrompt(request.prompt().trim());
        question.setPromptPinyin(request.promptPinyin());
        question.setPromptVi(request.promptVi());
        question.setAudioUrl(request.audioUrl());
        question.setImageUrl(request.imageUrl());
        question.setCorrectText(request.correctText());
        question.setExplanation(request.explanation());
        question.setPoints(request.points() != null ? request.points() : 1);
        question.setSortOrder(sortOrder != null ? sortOrder : 0);

        if (request.wordId() != null) {
            Word word = entityManager.find(Word.class, request.wordId());
            if (word == null) {
                throw new NotFoundException("Không tìm thấy từ vựng id=" + request.wordId());
            }
            question.setWord(word);
        } else {
            question.setWord(null);
        }

        // Thay thế toàn bộ phương án cũ TẠI CHỖ (clear + add) chứ không gán một List mới:
        // gán list mới sẽ làm mất PersistentBag mà Hibernate đang theo dõi và orphanRemoval
        // sẽ không xoá được các phương án cũ. Nhánh null chỉ là phòng thủ cho trường hợp
        // entity được dựng bằng constructor đầy đủ với options = null.
        if (question.getOptions() == null) {
            question.setOptions(new ArrayList<>());
        } else {
            question.getOptions().clear();
        }
        List<QuestionOptionRequest> options = request.options();
        if (options != null) {
            for (int index = 0; index < options.size(); index++) {
                QuestionOptionRequest optionRequest = options.get(index);
                QuestionOption option = QuestionOption.builder()
                        .content(optionRequest.content().trim())
                        .pinyin(optionRequest.pinyin())
                        .isCorrect(Boolean.TRUE.equals(optionRequest.isCorrect()))
                        .sortOrder(optionRequest.sortOrder() != null ? optionRequest.sortOrder() : index)
                        .build();
                question.addOption(option);
            }
        }
    }
}
