package com.example.bewebtiengtrung.module.course.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Gán lại toàn bộ danh sách từ vựng cho một bài học (chỉ admin).
 * Danh sách rỗng đồng nghĩa với việc gỡ hết từ vựng khỏi bài học.
 */
public record LessonWordsRequest(
        @NotNull(message = "Danh sách id từ vựng không được để trống")
        List<Long> wordIds
) {
}
