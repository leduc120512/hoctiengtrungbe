package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.course.dto.LessonDetailResponse;
import com.example.bewebtiengtrung.module.course.entity.Lesson;
import com.example.bewebtiengtrung.module.course.mapper.LessonMapper;
import com.example.bewebtiengtrung.module.course.repository.LessonRepository;
import com.example.bewebtiengtrung.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cài đặt nghiệp vụ đọc bài học. */
@Service
@Transactional(readOnly = true)
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    public LessonServiceImpl(LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }

    @Override
    public LessonDetailResponse getById(Long lessonId) {
        Lesson lesson = lessonRepository.findWithDetailsById(lessonId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài học với id: " + lessonId));
        if (!SecurityUtils.isAdmin() && !isVisibleToPublic(lesson)) {
            throw new NotFoundException("Không tìm thấy bài học với id: " + lessonId);
        }
        return lessonMapper.toDetail(lesson);
    }

    /** Bài học chỉ công khai khi chính nó và khóa học chứa nó đều đã được xuất bản. */
    private boolean isVisibleToPublic(Lesson lesson) {
        return Boolean.TRUE.equals(lesson.getPublished())
                && lesson.getCourse() != null
                && Boolean.TRUE.equals(lesson.getCourse().getPublished());
    }
}
