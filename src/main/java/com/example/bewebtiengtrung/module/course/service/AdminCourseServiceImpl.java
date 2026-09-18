package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.common.dto.IdResponse;
import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.common.exception.ConflictException;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.course.dto.CourseDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonGrammarRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonGrammarResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonWordsRequest;
import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.Lesson;
import com.example.bewebtiengtrung.module.course.entity.LessonGrammar;
import com.example.bewebtiengtrung.module.course.mapper.CourseMapper;
import com.example.bewebtiengtrung.module.course.mapper.LessonMapper;
import com.example.bewebtiengtrung.module.course.repository.CourseRepository;
import com.example.bewebtiengtrung.module.course.repository.CourseWordRepository;
import com.example.bewebtiengtrung.module.course.repository.LessonGrammarRepository;
import com.example.bewebtiengtrung.module.course.repository.LessonRepository;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Cài đặt nghiệp vụ quản trị nội dung khóa học. Mọi phương thức đều yêu cầu ROLE_ADMIN ở controller. */
@Service
@Transactional(readOnly = true)
public class AdminCourseServiceImpl implements AdminCourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonGrammarRepository lessonGrammarRepository;
    private final CourseWordRepository courseWordRepository;
    private final CourseMapper courseMapper;
    private final LessonMapper lessonMapper;

    public AdminCourseServiceImpl(CourseRepository courseRepository,
                                  LessonRepository lessonRepository,
                                  LessonGrammarRepository lessonGrammarRepository,
                                  CourseWordRepository courseWordRepository,
                                  CourseMapper courseMapper,
                                  LessonMapper lessonMapper) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.lessonGrammarRepository = lessonGrammarRepository;
        this.courseWordRepository = courseWordRepository;
        this.courseMapper = courseMapper;
        this.lessonMapper = lessonMapper;
    }

    @Override
    @Transactional
    public IdResponse createCourse(CourseRequest request) {
        String slug = request.slug().trim();
        if (courseRepository.existsBySlug(slug)) {
            throw new ConflictException("Slug khóa học đã tồn tại: " + slug);
        }
        Course saved = courseRepository.save(courseMapper.toEntity(request));
        return new IdResponse(saved.getId());
    }

    @Override
    @Transactional
    public CourseDetailResponse updateCourse(Long courseId, CourseRequest request) {
        Course course = requireCourse(courseId);
        String slug = request.slug().trim();
        if (courseRepository.existsBySlugAndIdNot(slug, courseId)) {
            throw new ConflictException("Slug khóa học đã tồn tại: " + slug);
        }
        courseMapper.applyRequest(course, request);
        Course saved = courseRepository.save(course);
        // Admin luôn xem được cả bài học chưa xuất bản.
        List<Lesson> lessons = lessonRepository.findByCourse(courseId, true);
        return courseMapper.toDetail(saved, lessons);
    }

    @Override
    @Transactional
    public MessageResponse deleteCourse(Long courseId) {
        courseRepository.delete(requireCourse(courseId));
        return new MessageResponse("Đã xóa khóa học");
    }

    private Course requireCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học với id: " + courseId));
    }

    private Lesson requireLessonWithDetails(Long lessonId) {
        return lessonRepository.findWithDetailsById(lessonId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài học với id: " + lessonId));
    }

    @Override
    @Transactional
    public IdResponse createLesson(LessonRequest request) {
        Course course = requireCourse(request.courseId());
        String slug = request.slug().trim();
        if (lessonRepository.existsByCourseIdAndSlug(course.getId(), slug)) {
            throw new ConflictException("Slug bài học đã tồn tại trong khóa học này: " + slug);
        }
        Lesson saved = lessonRepository.save(lessonMapper.toEntity(request, course));
        return new IdResponse(saved.getId());
    }

    @Override
    @Transactional
    public LessonDetailResponse updateLesson(Long lessonId, LessonRequest request) {
        Lesson lesson = requireLessonWithDetails(lessonId);
        // Cho phép chuyển bài học sang khóa học khác.
        Course targetCourse = lesson.getCourse() != null && lesson.getCourse().getId().equals(request.courseId())
                ? lesson.getCourse()
                : requireCourse(request.courseId());

        String slug = request.slug().trim();
        if (lessonRepository.existsByCourseIdAndSlugAndIdNot(targetCourse.getId(), slug, lessonId)) {
            throw new ConflictException("Slug bài học đã tồn tại trong khóa học này: " + slug);
        }

        lesson.setCourse(targetCourse);
        lessonMapper.applyRequest(lesson, request);
        return lessonMapper.toDetail(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public MessageResponse deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài học với id: " + lessonId));
        lessonRepository.delete(lesson);
        return new MessageResponse("Đã xóa bài học");
    }

    @Override
    @Transactional
    public IdResponse createGrammar(Long lessonId, LessonGrammarRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài học với id: " + lessonId));
        LessonGrammar saved = lessonGrammarRepository.save(lessonMapper.toGrammarEntity(request, lesson));
        return new IdResponse(saved.getId());
    }

    @Override
    @Transactional
    public LessonGrammarResponse updateGrammar(Long grammarId, LessonGrammarRequest request) {
        LessonGrammar grammar = lessonGrammarRepository.findWithLessonById(grammarId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy điểm ngữ pháp với id: " + grammarId));
        lessonMapper.applyGrammarRequest(grammar, request);
        return lessonMapper.toGrammarResponse(lessonGrammarRepository.save(grammar));
    }

    @Override
    @Transactional
    public MessageResponse deleteGrammar(Long grammarId) {
        LessonGrammar grammar = lessonGrammarRepository.findById(grammarId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy điểm ngữ pháp với id: " + grammarId));
        lessonGrammarRepository.delete(grammar);
        return new MessageResponse("Đã xóa điểm ngữ pháp");
    }

    @Override
    @Transactional
    public LessonDetailResponse setLessonWords(Long lessonId, LessonWordsRequest request) {
        Lesson lesson = requireLessonWithDetails(lessonId);

        // Loại bỏ id trùng nhưng vẫn giữ thứ tự client gửi lên.
        List<Long> wordIds = request.wordIds().stream()
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        Set<Word> words = new LinkedHashSet<>();
        if (!wordIds.isEmpty()) {
            Map<Long, Word> found = courseWordRepository.findAllById(wordIds).stream()
                    .collect(Collectors.toMap(Word::getId, Function.identity()));
            for (Long wordId : wordIds) {
                Word word = found.get(wordId);
                if (word == null) {
                    throw new NotFoundException("Không tìm thấy từ vựng với id: " + wordId);
                }
                words.add(word);
            }
        }

        // Thay thế toàn bộ tập từ vựng; cột sort_order của bảng nối không được ánh xạ
        // nên Hibernate chỉ ghi (lesson_id, word_id) và cột đó nhận giá trị mặc định 0.
        lesson.getWords().clear();
        lesson.getWords().addAll(words);

        return lessonMapper.toDetail(lessonRepository.save(lesson));
    }
}
