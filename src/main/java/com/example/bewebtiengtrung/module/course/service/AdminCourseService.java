package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.common.dto.IdResponse;
import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonGrammarRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonGrammarResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonWordsRequest;

/** Nghiệp vụ quản trị nội dung khóa học / bài học / ngữ pháp (chỉ dành cho ROLE_ADMIN). */
public interface AdminCourseService {

    IdResponse createCourse(CourseRequest request);

    CourseDetailResponse updateCourse(Long courseId, CourseRequest request);

    /** Xóa khóa học kéo theo toàn bộ bài học, ngữ pháp và bản ghi ghi danh (cascade ở DB). */
    MessageResponse deleteCourse(Long courseId);

    IdResponse createLesson(LessonRequest request);

    LessonDetailResponse updateLesson(Long lessonId, LessonRequest request);

    MessageResponse deleteLesson(Long lessonId);

    IdResponse createGrammar(Long lessonId, LessonGrammarRequest request);

    LessonGrammarResponse updateGrammar(Long grammarId, LessonGrammarRequest request);

    MessageResponse deleteGrammar(Long grammarId);

    /** Gán lại toàn bộ danh sách từ vựng cho một bài học (thay thế, không cộng dồn). */
    LessonDetailResponse setLessonWords(Long lessonId, LessonWordsRequest request);
}
