package com.example.bewebtiengtrung.module.course.controller;

import com.example.bewebtiengtrung.module.course.dto.LessonDetailResponse;
import com.example.bewebtiengtrung.module.course.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API công khai cho bài học. */
@RestController
@RequestMapping("/api/v1/lessons")
@Tag(name = "Bài học", description = "Xem nội dung bài học, điểm ngữ pháp và từ vựng")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết bài học gồm nội dung, ngữ pháp và danh sách từ vựng")
    public ResponseEntity<LessonDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getById(id));
    }
}
