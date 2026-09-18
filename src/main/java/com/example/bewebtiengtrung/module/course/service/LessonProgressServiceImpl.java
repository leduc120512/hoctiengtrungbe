package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.course.dto.CourseProgressResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonProgressRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonProgressResponse;
import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.Enrollment;
import com.example.bewebtiengtrung.module.course.entity.Lesson;
import com.example.bewebtiengtrung.module.course.entity.ProgressStatus;
import com.example.bewebtiengtrung.module.course.entity.UserLessonProgress;
import com.example.bewebtiengtrung.module.course.mapper.LessonProgressMapper;
import com.example.bewebtiengtrung.module.course.repository.CourseRepository;
import com.example.bewebtiengtrung.module.course.repository.EnrollmentRepository;
import com.example.bewebtiengtrung.module.course.repository.LessonRepository;
import com.example.bewebtiengtrung.module.course.repository.UserLessonProgressRepository;
import com.example.bewebtiengtrung.module.user.repository.UserRepository;
import com.example.bewebtiengtrung.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/** Cài đặt nghiệp vụ theo dõi tiến độ học. */
@Service
@Transactional(readOnly = true)
public class LessonProgressServiceImpl implements LessonProgressService {

    private final UserLessonProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final LessonProgressMapper progressMapper;

    public LessonProgressServiceImpl(UserLessonProgressRepository progressRepository,
                                     LessonRepository lessonRepository,
                                     CourseRepository courseRepository,
                                     EnrollmentRepository enrollmentRepository,
                                     UserRepository userRepository,
                                     LessonProgressMapper progressMapper) {
        this.progressRepository = progressRepository;
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.progressMapper = progressMapper;
    }

    @Override
    @Transactional
    public LessonProgressResponse upsertLessonProgress(Long lessonId, LessonProgressRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Lesson lesson = lessonRepository.findWithCourseById(lessonId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài học với id: " + lessonId));
        if (!SecurityUtils.isAdmin() && !isVisibleToPublic(lesson)) {
            throw new NotFoundException("Không tìm thấy bài học với id: " + lessonId);
        }

        Instant now = Instant.now();
        // Upsert: lấy bản ghi cũ theo cặp (userId, lessonId), nếu chưa có thì tạo mới.
        UserLessonProgress progress = progressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    UserLessonProgress created = new UserLessonProgress();
                    created.setUser(userRepository.getReferenceById(userId));
                    created.setLesson(lesson);
                    created.setProgressPercent(0);
                    return created;
                });

        ProgressStatus status = request.status();
        progress.setStatus(status);
        progress.setLastViewedAt(now);

        if (status == ProgressStatus.COMPLETED) {
            // Hoàn thành thì luôn ép 100% và ghi nhận mốc thời gian hoàn thành lần đầu.
            progress.setProgressPercent(100);
            if (progress.getCompletedAt() == null) {
                progress.setCompletedAt(now);
            }
        } else {
            progress.setCompletedAt(null);
            progress.setProgressPercent(resolvePercent(status, request.progressPercent(), progress.getProgressPercent()));
        }

        UserLessonProgress saved = progressRepository.save(progress);

        Course course = lesson.getCourse();
        if (course != null) {
            syncEnrollmentCompletion(userId, course.getId());
        }
        return progressMapper.toResponse(saved);
    }

    @Override
    public CourseProgressResponse getCourseProgress(Long courseId) {
        Long userId = SecurityUtils.currentUserId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học với id: " + courseId));
        if (!SecurityUtils.isAdmin() && !Boolean.TRUE.equals(course.getPublished())) {
            throw new NotFoundException("Không tìm thấy khóa học với id: " + courseId);
        }

        long totalLessons = lessonRepository.countByCourseIdAndPublishedTrue(courseId);
        long completedLessons = progressRepository.countByUserAndCourseAndStatus(
                userId, courseId, ProgressStatus.COMPLETED);

        return new CourseProgressResponse(
                course.getId(),
                course.getSlug(),
                course.getTitle(),
                completedLessons,
                totalLessons,
                toPercent(completedLessons, totalLessons)
        );
    }

    /** Phần trăm khi chưa hoàn thành: ưu tiên giá trị client gửi, NOT_STARTED luôn về 0. */
    private int resolvePercent(ProgressStatus status, Integer requested, Integer current) {
        if (status == ProgressStatus.NOT_STARTED) {
            return 0;
        }
        if (requested != null) {
            return requested;
        }
        return current != null ? current : 0;
    }

    /**
     * Đồng bộ mốc hoàn thành khóa học: nếu người dùng đã ghi danh và hoàn thành hết
     * số bài học đã xuất bản thì đóng dấu completedAt, ngược lại xóa dấu đó đi.
     */
    private void syncEnrollmentCompletion(Long userId, Long courseId) {
        Optional<Enrollment> optional = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (optional.isEmpty()) {
            return;
        }
        Enrollment enrollment = optional.get();
        long totalLessons = lessonRepository.countByCourseIdAndPublishedTrue(courseId);
        long completedLessons = progressRepository.countByUserAndCourseAndStatus(
                userId, courseId, ProgressStatus.COMPLETED);

        if (totalLessons > 0 && completedLessons >= totalLessons) {
            if (enrollment.getCompletedAt() == null) {
                enrollment.setCompletedAt(Instant.now());
            }
        } else {
            enrollment.setCompletedAt(null);
        }
        enrollmentRepository.save(enrollment);
    }

    private int toPercent(long completed, long total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.round(completed * 100.0 / total);
    }

    private boolean isVisibleToPublic(Lesson lesson) {
        return Boolean.TRUE.equals(lesson.getPublished())
                && lesson.getCourse() != null
                && Boolean.TRUE.equals(lesson.getCourse().getPublished());
    }
}
