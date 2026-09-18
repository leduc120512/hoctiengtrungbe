package com.example.bewebtiengtrung.module.course.controller;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseSummaryResponse;
import com.example.bewebtiengtrung.module.course.entity.CourseLevel;
import com.example.bewebtiengtrung.module.course.service.CourseService;
import com.example.bewebtiengtrung.module.course.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** API công khai cho khóa học. */
@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Khóa học", description = "Xem danh sách, chi tiết khóa học và ghi danh")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public CourseController(CourseService courseService, EnrollmentService enrollmentService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    @Operation(summary = "Danh sách khóa học có phân trang, lọc theo cấp độ HSK và từ khóa")
    public ResponseEntity<PageResponse<CourseSummaryResponse>> search(
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = {"sortOrder", "id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(courseService.search(level, q, pageable));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Chi tiết khóa học theo slug kèm danh sách bài học")
    public ResponseEntity<CourseDetailResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(courseService.getBySlug(slug));
    }

    @PostMapping("/{id}/enroll")
    @Operation(summary = "Ghi danh vào khóa học cho người dùng đang đăng nhập")
    public ResponseEntity<MessageResponse> enroll(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.enroll(id));
    }

    @DeleteMapping("/{id}/enroll")
    @Operation(summary = "Hủy ghi danh khỏi khóa học")
    public ResponseEntity<MessageResponse> unenroll(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.unenroll(id));
    }
}
