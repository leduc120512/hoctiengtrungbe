package com.example.bewebtiengtrung.module.course.mapper;

import com.example.bewebtiengtrung.module.course.dto.CourseDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseRequest;
import com.example.bewebtiengtrung.module.course.dto.CourseSummaryResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonSummaryResponse;
import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.Lesson;
import org.springframework.stereotype.Component;

import java.util.List;

/** Chuyển đổi giữa entity Course và các DTO tương ứng. */
@Component
public class CourseMapper {

    private final LessonMapper lessonMapper;

    public CourseMapper(LessonMapper lessonMapper) {
        this.lessonMapper = lessonMapper;
    }

    /**
     * @param lessonCount số bài học đã được đếm sẵn theo lô để tránh N+1
     */
    public CourseSummaryResponse toSummary(Course course, long lessonCount) {
        return new CourseSummaryResponse(
                course.getId(),
                course.getSlug(),
                course.getTitle(),
                course.getDescription(),
                course.getLevel(),
                course.getThumbnailUrl(),
                course.getPublished(),
                course.getSortOrder(),
                course.getEstimatedMinutes(),
                lessonCount
        );
    }

    /**
     * @param lessons danh sách bài học đã được lọc sẵn theo quyền của người gọi
     */
    public CourseDetailResponse toDetail(Course course, List<Lesson> lessons) {
        List<LessonSummaryResponse> lessonSummaries = lessons.stream()
                .map(lessonMapper::toSummary)
                .toList();
        return new CourseDetailResponse(
                course.getId(),
                course.getSlug(),
                course.getTitle(),
                course.getDescription(),
                course.getLevel(),
                course.getThumbnailUrl(),
                course.getPublished(),
                course.getSortOrder(),
                course.getEstimatedMinutes(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                lessonSummaries
        );
    }

    /** Tạo entity mới từ request; các trường tùy chọn nhận giá trị mặc định giống DDL. */
    public Course toEntity(CourseRequest request) {
        Course course = new Course();
        applyRequest(course, request);
        return course;
    }

    /** Ghi đè dữ liệu từ request lên entity đang được persistence context quản lý. */
    public void applyRequest(Course course, CourseRequest request) {
        course.setSlug(request.slug().trim());
        course.setTitle(request.title().trim());
        course.setDescription(request.description());
        course.setLevel(request.level());
        course.setThumbnailUrl(request.thumbnailUrl());
        course.setPublished(request.published() != null ? request.published() : Boolean.FALSE);
        course.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        course.setEstimatedMinutes(request.estimatedMinutes());
    }
}
