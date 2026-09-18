package com.example.bewebtiengtrung.module.course.controller;

import com.example.bewebtiengtrung.common.dto.IdResponse;
import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.CourseRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonDetailResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonGrammarRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonGrammarResponse;
import com.example.bewebtiengtrung.module.course.dto.LessonRequest;
import com.example.bewebtiengtrung.module.course.dto.LessonWordsRequest;
import com.example.bewebtiengtrung.module.course.service.AdminCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API quản trị nội dung khóa học, bài học và điểm ngữ pháp. */
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Quản trị khóa học", description = "CRUD khóa học, bài học, ngữ pháp và gán từ vựng")
public class AdminCourseController {

    private final AdminCourseService adminCourseService;

    public AdminCourseController(AdminCourseService adminCourseService) {
        this.adminCourseService = adminCourseService;
    }

    @PostMapping("/courses")
    @Operation(summary = "Tạo khóa học mới")
    public ResponseEntity<IdResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCourseService.createCourse(request));
    }

    @PutMapping("/courses/{id}")
    @Operation(summary = "Cập nhật thông tin khóa học")
    public ResponseEntity<CourseDetailResponse> updateCourse(@PathVariable Long id,
                                                             @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(adminCourseService.updateCourse(id, request));
    }

    @DeleteMapping("/courses/{id}")
    @Operation(summary = "Xóa khóa học cùng toàn bộ bài học bên trong")
    public ResponseEntity<MessageResponse> deleteCourse(@PathVariable Long id) {
        return ResponseEntity.ok(adminCourseService.deleteCourse(id));
    }

    @PostMapping("/lessons")
    @Operation(summary = "Tạo bài học mới cho một khóa học")
    public ResponseEntity<IdResponse> createLesson(@Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCourseService.createLesson(request));
    }

    @PutMapping("/lessons/{id}")
    @Operation(summary = "Cập nhật bài học")
    public ResponseEntity<LessonDetailResponse> updateLesson(@PathVariable Long id,
                                                             @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(adminCourseService.updateLesson(id, request));
    }

    @DeleteMapping("/lessons/{id}")
    @Operation(summary = "Xóa bài học")
    public ResponseEntity<MessageResponse> deleteLesson(@PathVariable Long id) {
        return ResponseEntity.ok(adminCourseService.deleteLesson(id));
    }

    @PostMapping("/lessons/{id}/grammar")
    @Operation(summary = "Thêm một điểm ngữ pháp vào bài học")
    public ResponseEntity<IdResponse> createGrammar(@PathVariable Long id,
                                                    @Valid @RequestBody LessonGrammarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCourseService.createGrammar(id, request));
    }

    @PutMapping("/grammar/{id}")
    @Operation(summary = "Cập nhật một điểm ngữ pháp")
    public ResponseEntity<LessonGrammarResponse> updateGrammar(@PathVariable Long id,
                                                               @Valid @RequestBody LessonGrammarRequest request) {
        return ResponseEntity.ok(adminCourseService.updateGrammar(id, request));
    }

    @DeleteMapping("/grammar/{id}")
    @Operation(summary = "Xóa một điểm ngữ pháp")
    public ResponseEntity<MessageResponse> deleteGrammar(@PathVariable Long id) {
        return ResponseEntity.ok(adminCourseService.deleteGrammar(id));
    }

    @PutMapping("/lessons/{id}/words")
    @Operation(summary = "Gán lại toàn bộ danh sách từ vựng cho bài học")
    public ResponseEntity<LessonDetailResponse> setLessonWords(@PathVariable Long id,
                                                               @Valid @RequestBody LessonWordsRequest request) {
        return ResponseEntity.ok(adminCourseService.setLessonWords(id, request));
    }
}
