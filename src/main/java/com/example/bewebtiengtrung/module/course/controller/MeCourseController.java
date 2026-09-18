package com.example.bewebtiengtrung.module.course.controller;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseProgressResponse;
import com.example.bewebtiengtrung.module.course.dto.EnrollmentResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonProgressRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonProgressResponse;
import com.example.bewebtiengtrung.module.course.service.EnrollmentService;
import com.example.bewebtiengtrung.module.course.service.LessonProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API dữ liệu học tập của chính người dùng đang đăng nhập. */
@RestController
@RequestMapping("/api/v1/me")
@Tag(name = "Học tập cá nhân", description = "Khóa học đã ghi danh và tiến độ học của người dùng hiện tại")
public class MeCourseController {

    private final EnrollmentService enrollmentService;
    private final LessonProgressService lessonProgressService;

    public MeCourseController(EnrollmentService enrollmentService,
                              LessonProgressService lessonProgressService) {
        this.enrollmentService = enrollmentService;
        this.lessonProgressService = lessonProgressService;
    }

    @GetMapping("/enrollments")
    @Operation(summary = "Danh sách khóa học mà tôi đã ghi danh")
    public ResponseEntity<PageResponse<EnrollmentResponse>> myEnrollments(
            @PageableDefault(size = 20, sort = "enrolledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.myEnrollments(pageable));
    }

    @PutMapping("/lessons/{lessonId}/progress")
    @Operation(summary = "Cập nhật (tạo mới nếu chưa có) tiến độ học của một bài học")
    public ResponseEntity<LessonProgressResponse> updateLessonProgress(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonProgressRequest request) {
        return ResponseEntity.ok(lessonProgressService.upsertLessonProgress(lessonId, request));
    }

    @GetMapping("/courses/{courseId}/progress")
    @Operation(summary = "Tiến độ tổng thể của tôi trên một khóa học")
    public ResponseEntity<CourseProgressResponse> courseProgress(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonProgressService.getCourseProgress(courseId));
    }
}
