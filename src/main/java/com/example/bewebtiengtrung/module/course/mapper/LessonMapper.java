package com.example.bewebtiengtrung.module.course.mapper;

import com.example.bewebtiengtrung.module.course.dto.LessonDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonGrammarRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonGrammarResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonSummaryResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonWordResponse;
import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.Lesson;
import com.example.bewebtiengtrung.module.course.entity.LessonGrammar;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/** Chuyển đổi giữa entity Lesson / LessonGrammar và các DTO tương ứng. */
@Component
public class LessonMapper {

    public LessonSummaryResponse toSummary(Lesson lesson) {
        return new LessonSummaryResponse(
                lesson.getId(),
                lesson.getSlug(),
                lesson.getTitle(),
                lesson.getSummary(),
                lesson.getSortOrder(),
                lesson.getEstimatedMinutes(),
                lesson.getPublished()
        );
    }

    /**
     * Bài học chi tiết. Phải gọi trong ngữ cảnh giao dịch với các quan hệ đã được nạp
     * (xem LessonRepository#findWithDetailsById).
     */
    public LessonDetailResponse toDetail(Lesson lesson) {
        Course course = lesson.getCourse();
        // grammarPoints là Set nên phải sắp xếp tường minh; thêm id làm khóa phụ để thứ tự
        // trả về luôn ổn định khi nhiều điểm ngữ pháp có cùng sortOrder.
        Comparator<LessonGrammar> grammarOrder =
                Comparator.comparingInt((LessonGrammar g) -> g.getSortOrder() != null ? g.getSortOrder() : 0)
                        .thenComparing(g -> g.getId() != null ? g.getId() : Long.MAX_VALUE);
        List<LessonGrammarResponse> grammar = lesson.getGrammarPoints().stream()
                .sorted(grammarOrder)
                .map(this::toGrammarResponse)
                .toList();
        // Bảng nối lesson_words có cột sort_order nhưng không được ánh xạ (xem chú thích ở Lesson),
        // vì vậy từ vựng được sắp xếp theo id để kết quả trả về luôn ổn định.
        List<LessonWordResponse> words = lesson.getWords().stream()
                .sorted(Comparator.comparing(Word::getId))
                .map(this::toWordResponse)
                .toList();
        return new LessonDetailResponse(
                lesson.getId(),
                course != null ? course.getId() : null,
                course != null ? course.getSlug() : null,
                course != null ? course.getTitle() : null,
                lesson.getSlug(),
                lesson.getTitle(),
                lesson.getSummary(),
                lesson.getContent(),
                lesson.getVideoUrl(),
                lesson.getAudioUrl(),
                lesson.getSortOrder(),
                lesson.getEstimatedMinutes(),
                lesson.getPublished(),
                lesson.getCreatedAt(),
                lesson.getUpdatedAt(),
                grammar,
                words
        );
    }

    public LessonGrammarResponse toGrammarResponse(LessonGrammar grammar) {
        return new LessonGrammarResponse(
                grammar.getId(),
                grammar.getLesson() != null ? grammar.getLesson().getId() : null,
                grammar.getTitle(),
                grammar.getStructure(),
                grammar.getExplanation(),
                grammar.getExampleZh(),
                grammar.getExampleVi(),
                grammar.getSortOrder()
        );
    }

    public LessonWordResponse toWordResponse(Word word) {
        return new LessonWordResponse(
                word.getId(),
                word.getSimplified(),
                word.getTraditional(),
                word.getPinyin(),
                word.getMeaningVi(),
                word.getHskLevel(),
                word.getAudioUrl()
        );
    }

    /** Tạo entity bài học mới; khóa học do service gán để đảm bảo đã tồn tại. */
    public Lesson toEntity(LessonRequest request, Course course) {
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        applyRequest(lesson, request);
        return lesson;
    }

    /** Ghi đè dữ liệu từ request lên entity bài học. */
    public void applyRequest(Lesson lesson, LessonRequest request) {
        lesson.setSlug(request.slug().trim());
        lesson.setTitle(request.title().trim());
        lesson.setSummary(request.summary());
        lesson.setContent(request.content());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setAudioUrl(request.audioUrl());
        lesson.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        lesson.setEstimatedMinutes(request.estimatedMinutes());
        lesson.setPublished(request.published() != null ? request.published() : Boolean.FALSE);
    }

    public LessonGrammar toGrammarEntity(LessonGrammarRequest request, Lesson lesson) {
        LessonGrammar grammar = new LessonGrammar();
        grammar.setLesson(lesson);
        applyGrammarRequest(grammar, request);
        return grammar;
    }

    public void applyGrammarRequest(LessonGrammar grammar, LessonGrammarRequest request) {
        grammar.setTitle(request.title().trim());
        grammar.setStructure(request.structure());
        grammar.setExplanation(request.explanation());
        grammar.setExampleZh(request.exampleZh());
        grammar.setExampleVi(request.exampleVi());
        grammar.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
    }
}
