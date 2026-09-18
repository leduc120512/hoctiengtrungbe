package com.example.bewebtiengtrung.module.srs.service;

import com.example.bewebtiengtrung.module.srs.entity.Rating;
import com.example.bewebtiengtrung.module.srs.entity.ReviewStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Cài đặt thuần tuý (không phụ thuộc DB, không gây tác dụng phụ) của thuật toán SM-2.
 *
 * <p>Nhờ tách riêng khỏi tầng service nghiệp vụ, lớp này có thể được kiểm thử đơn vị
 * trực tiếp bằng {@code new Sm2Calculator()}.</p>
 *
 * <p>Công thức:</p>
 * <pre>
 * q = rating.quality  (AGAIN=0, HARD=3, GOOD=4, EASY=5)
 *
 * nếu q &lt; 3:
 *     repetitions  = 0
 *     intervalDays = 1
 *     lapses       = lapses + 1
 *     status       = RELEARNING
 * ngược lại:
 *     repetitions = repetitions + 1
 *     repetitions == 1 -&gt; intervalDays = 1
 *     repetitions == 2 -&gt; intervalDays = 6
 *     ngược lại        -&gt; intervalDays = round(intervalDays * easeFactor)   // dùng EF CŨ
 *     status = REVIEW
 *
 * easeFactor = max(1.3, easeFactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)))
 * dueAt      = now + intervalDays ngày, riêng AGAIN thì dueAt = now + 10 phút
 * </pre>
 */
@Component
public class Sm2Calculator {

    /** Hệ số dễ khởi tạo cho thẻ mới. */
    public static final double DEFAULT_EASE_FACTOR = 2.5d;

    /** Ngưỡng dưới của hệ số dễ theo SM-2. */
    public static final double MIN_EASE_FACTOR = 1.3d;

    /** Ngưỡng "quên": q nhỏ hơn giá trị này thì phải học lại. */
    private static final int FORGOT_QUALITY_THRESHOLD = 3;

    /** Với đánh giá AGAIN, thẻ quay lại ngay trong phiên học sau 10 phút. */
    private static final long RELEARN_DELAY_MINUTES = 10L;

    /**
     * Kết quả tính toán SM-2 – bất biến, không tham chiếu tới entity nào.
     *
     * @param easeFactor   hệ số dễ mới (đã kẹp tối thiểu 1.3)
     * @param intervalDays khoảng cách ngày mới (tối thiểu 1)
     * @param repetitions  số lần đúng liên tiếp mới
     * @param lapses       số lần quên luỹ kế mới
     * @param status       trạng thái ôn tập mới
     * @param dueAt        thời điểm đến hạn kế tiếp
     */
    public record Sm2Result(double easeFactor,
                            int intervalDays,
                            int repetitions,
                            int lapses,
                            ReviewStatus status,
                            Instant dueAt) {
    }

    /**
     * Áp dụng SM-2 cho một lần ôn tập.
     *
     * @param easeFactor   hệ số dễ hiện tại
     * @param intervalDays khoảng cách ngày hiện tại
     * @param repetitions  số lần đúng liên tiếp hiện tại
     * @param lapses       số lần quên hiện tại
     * @param rating       mức đánh giá của người dùng
     * @param now          mốc thời gian coi là "bây giờ" (tiêm vào để dễ kiểm thử)
     * @return trạng thái mới, không thay đổi bất kỳ tham số đầu vào nào
     */
    public Sm2Result apply(double easeFactor,
                           int intervalDays,
                           int repetitions,
                           int lapses,
                           Rating rating,
                           Instant now) {
        Objects.requireNonNull(rating, "rating không được null");
        Objects.requireNonNull(now, "now không được null");

        int quality = rating.getQuality();
        int nextRepetitions;
        int nextIntervalDays;
        int nextLapses;
        ReviewStatus nextStatus;
        Instant nextDueAt;

        if (quality < FORGOT_QUALITY_THRESHOLD) {
            // Quên: đặt lại chuỗi đúng, tăng số lần quên, học lại ngay trong phiên.
            nextRepetitions = 0;
            nextIntervalDays = 1;
            nextLapses = lapses + 1;
            nextStatus = ReviewStatus.RELEARNING;
            nextDueAt = now.plus(RELEARN_DELAY_MINUTES, ChronoUnit.MINUTES);
        } else {
            nextRepetitions = repetitions + 1;
            if (nextRepetitions == 1) {
                nextIntervalDays = 1;
            } else if (nextRepetitions == 2) {
                nextIntervalDays = 6;
            } else {
                // Nhân với hệ số dễ CŨ rồi làm tròn theo đúng mô tả của SM-2.
                nextIntervalDays = (int) Math.round(intervalDays * easeFactor);
            }
            // Chặn dưới: khoảng cách luôn tối thiểu 1 ngày.
            if (nextIntervalDays < 1) {
                nextIntervalDays = 1;
            }
            nextLapses = lapses;
            nextStatus = ReviewStatus.REVIEW;
            nextDueAt = now.plus(nextIntervalDays, ChronoUnit.DAYS);
        }

        double delta = 0.1d - (5 - quality) * (0.08d + (5 - quality) * 0.02d);
        double nextEaseFactor = Math.max(MIN_EASE_FACTOR, easeFactor + delta);

        return new Sm2Result(nextEaseFactor, nextIntervalDays, nextRepetitions, nextLapses, nextStatus, nextDueAt);
    }
}
