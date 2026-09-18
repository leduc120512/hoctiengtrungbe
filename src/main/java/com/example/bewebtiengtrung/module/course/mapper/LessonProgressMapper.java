package com.example.bewebtiengtrung.module.course.mapper;

import com.example.bewebtiengtrung.module.course.dto.LessonProgressResponse;
import com.example.bewebtiengtrung.module.course.entity.Lesson;
import com.example.bewebtiengtrung.module.course.entity.UserLessonProgress;
import org.springframework.stereotype.Component;

/** Chuyển đổi entity UserLessonProgress sang DTO trả về cho client. */
@Component
public class LessonProgressMapper {

    public LessonProgressResponse toResponse(UserLessonProgress progress) {
        Lesson lesson = progress.getLesson();
        return new LessonProgressResponse(
                progress.getId(),
                lesson.getId(),
                lesson.getTitle(),
                lesson.getCourse() != null ? lesson.getCourse().getId() : null,
                progress.getStatus(),
                progress.getProgressPercent(),
                progress.getLastViewedAt(),
                progress.getCompletedAt()
        );
    }
}
