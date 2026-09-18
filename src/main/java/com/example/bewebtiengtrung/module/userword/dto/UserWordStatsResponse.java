package com.example.bewebtiengtrung.module.userword.dto;

import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * Thống kê sổ từ đã học của người dùng hiện tại.
 *
 * @param total           tổng số từ trong sổ
 * @param byStatus        số từ theo từng trạng thái (luôn có đủ 3 khoá, thiếu thì bằng 0)
 * @param byHskLevel      số từ theo cấp độ HSK của từ gốc, sắp xếp theo cấp tăng dần
 * @param learnedThisWeek số từ có {@code learnedAt} trong 7 ngày gần nhất
 */
@Schema(description = "Thống kê sổ từ đã học")
public record UserWordStatsResponse(
        long total,
        Map<UserWordStatus, Long> byStatus,
        Map<Integer, Long> byHskLevel,
        long learnedThisWeek
) {
}
