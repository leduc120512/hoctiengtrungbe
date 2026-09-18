package com.example.bewebtiengtrung.module.course.dto;

import com.example.bewebtiengtrung.module.course.entity.ProgressStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Yêu cầu cập nhật (upsert) tiến độ một bài học.
 * Khi status = COMPLETED thì progressPercent luôn được ép về 100 ở tầng service.
 */
public record LessonProgressRequest(
        @NotNull(message = "Trạng thái học không được để trống")
        ProgressStatus status,

        @Min(value = 0, message = "Phần trăm tiến độ tối thiểu là 0")
        @Max(value = 100, message = "Phần trăm tiến độ tối đa là 100")
        Integer progressPercent
) {
}
