package com.example.bewebtiengtrung.module.srs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Thống kê học tập của người dùng hiện tại.
 *
 * @param newCount      số thẻ ở trạng thái NEW
 * @param learningCount số thẻ đang học lại (LEARNING + RELEARNING)
 * @param reviewCount   số thẻ đã thuộc, đang ôn theo chu kỳ (REVIEW)
 * @param dueToday      số thẻ đến hạn tính tới hết ngày hôm nay (UTC)
 * @param reviewedToday số lượt ôn đã thực hiện trong ngày hôm nay (UTC)
 * @param streakDays    số ngày học liên tiếp tính ngược từ hôm nay
 */
@Schema(description = "Thống kê tiến độ ôn tập")
public record SrsStatsResponse(
        long newCount,
        long learningCount,
        long reviewCount,
        long dueToday,
        long reviewedToday,
        int streakDays
) {
}
