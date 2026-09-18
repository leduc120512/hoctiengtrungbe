package com.example.bewebtiengtrung.module.srs.service;

import com.example.bewebtiengtrung.module.srs.entity.Rating;
import com.example.bewebtiengtrung.module.srs.entity.ReviewStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Kiểm thử thuật toán SM-2.
 *
 * <p>Đây là phần logic lõi quyết định người học gặp lại một thẻ vào lúc nào,
 * nên mọi con số dưới đây đều được đối chiếu trực tiếp với công thức gốc:
 * <pre>
 *   EF' = max(1.3, EF + (0.1 − (5−q) × (0.08 + (5−q) × 0.02)))
 * </pre>
 */
class Sm2CalculatorTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final double EPS = 1e-9;

    private final Sm2Calculator calculator = new Sm2Calculator();

    @Nested
    @DisplayName("Chuỗi ôn tập thuận lợi")
    class ChuoiThuanLoi {

        @Test
        @DisplayName("GOOD ba lần liên tiếp cho khoảng cách 1 → 6 → 15 ngày")
        void ba_lan_good_lien_tiep() {
            // Lần 1: thẻ mới, trả lời đúng -> 1 ngày
            Sm2Calculator.Sm2Result r1 =
                    calculator.apply(2.5d, 0, 0, 0, Rating.GOOD, NOW);
            assertThat(r1.intervalDays()).isEqualTo(1);
            assertThat(r1.repetitions()).isEqualTo(1);
            assertThat(r1.status()).isEqualTo(ReviewStatus.REVIEW);
            assertThat(r1.dueAt()).isEqualTo(NOW.plus(1, ChronoUnit.DAYS));

            // Lần 2: -> 6 ngày (giá trị cố định của SM-2)
            Sm2Calculator.Sm2Result r2 = calculator.apply(
                    r1.easeFactor(), r1.intervalDays(), r1.repetitions(), r1.lapses(), Rating.GOOD, NOW);
            assertThat(r2.intervalDays()).isEqualTo(6);
            assertThat(r2.repetitions()).isEqualTo(2);

            // Lần 3: -> round(6 × 2.5) = 15 ngày
            Sm2Calculator.Sm2Result r3 = calculator.apply(
                    r2.easeFactor(), r2.intervalDays(), r2.repetitions(), r2.lapses(), Rating.GOOD, NOW);
            assertThat(r3.intervalDays()).isEqualTo(15);
            assertThat(r3.repetitions()).isEqualTo(3);
            assertThat(r3.dueAt()).isEqualTo(NOW.plus(15, ChronoUnit.DAYS));
        }

        @Test
        @DisplayName("GOOD (q=4) giữ nguyên hệ số dễ vì phần bù của công thức bằng 0")
        void good_giu_nguyen_ease_factor() {
            Sm2Calculator.Sm2Result r = calculator.apply(2.5d, 0, 0, 0, Rating.GOOD, NOW);
            // q=4: 0.1 − 1 × (0.08 + 1 × 0.02) = 0.1 − 0.1 = 0
            assertThat(r.easeFactor()).isCloseTo(2.5d, org.assertj.core.data.Offset.offset(EPS));
        }

        @Test
        @DisplayName("EASY (q=5) làm hệ số dễ tăng lên 2.6")
        void easy_tang_ease_factor() {
            Sm2Calculator.Sm2Result r = calculator.apply(2.5d, 0, 0, 0, Rating.EASY, NOW);
            // q=5: 0.1 − 0 = +0.1
            assertThat(r.easeFactor()).isCloseTo(2.6d, org.assertj.core.data.Offset.offset(EPS));
            assertThat(r.status()).isEqualTo(ReviewStatus.REVIEW);
        }

        @Test
        @DisplayName("HARD (q=3) làm hệ số dễ giảm còn 2.36 nhưng vẫn tính là nhớ")
        void hard_giam_ease_factor_nhung_van_la_nho() {
            Sm2Calculator.Sm2Result r = calculator.apply(2.5d, 0, 0, 0, Rating.HARD, NOW);
            // q=3: 0.1 − 2 × (0.08 + 2 × 0.02) = 0.1 − 0.24 = −0.14
            assertThat(r.easeFactor()).isCloseTo(2.36d, org.assertj.core.data.Offset.offset(EPS));
            assertThat(r.repetitions()).isEqualTo(1);
            assertThat(r.lapses()).isZero();
            assertThat(r.status()).isEqualTo(ReviewStatus.REVIEW);
        }
    }

    @Nested
    @DisplayName("Khi người học quên")
    class KhiQuen {

        @Test
        @DisplayName("AGAIN đặt lại chuỗi đúng, tăng lapses và chuyển sang RELEARNING")
        void again_dat_lai_chuoi() {
            // Thẻ đang ở trạng thái tốt: 3 lần đúng, cách 15 ngày
            Sm2Calculator.Sm2Result r =
                    calculator.apply(2.5d, 15, 3, 0, Rating.AGAIN, NOW);

            assertThat(r.repetitions()).isZero();
            assertThat(r.lapses()).isEqualTo(1);
            assertThat(r.status()).isEqualTo(ReviewStatus.RELEARNING);
            // q=0: 0.1 − 5 × (0.08 + 5 × 0.02) = 0.1 − 0.9 = −0.8
            assertThat(r.easeFactor()).isCloseTo(1.7d, org.assertj.core.data.Offset.offset(EPS));
        }

        @Test
        @DisplayName("AGAIN hẹn gặp lại sau 10 phút để học lại ngay trong phiên")
        void again_hen_lai_sau_10_phut() {
            Sm2Calculator.Sm2Result r =
                    calculator.apply(2.5d, 15, 3, 0, Rating.AGAIN, NOW);

            assertThat(r.dueAt()).isEqualTo(NOW.plus(10, ChronoUnit.MINUTES));
            // intervalDays vẫn lưu 1 để lần đúng kế tiếp bắt đầu lại từ đầu chuỗi
            assertThat(r.intervalDays()).isEqualTo(1);
        }

        @Test
        @DisplayName("Quên nhiều lần cũng không kéo hệ số dễ xuống dưới ngưỡng 1.3")
        void ease_factor_khong_xuong_duoi_1_3() {
            double ef = 2.5d;
            for (int i = 0; i < 20; i++) {
                ef = calculator.apply(ef, 1, 0, i, Rating.AGAIN, NOW).easeFactor();
            }
            assertThat(ef).isGreaterThanOrEqualTo(Sm2Calculator.MIN_EASE_FACTOR);
            assertThat(ef).isCloseTo(Sm2Calculator.MIN_EASE_FACTOR, org.assertj.core.data.Offset.offset(EPS));
        }
    }

    @Nested
    @DisplayName("Ràng buộc chung")
    class RangBuoc {

        @Test
        @DisplayName("Khoảng cách luôn tối thiểu 1 ngày, không bao giờ bằng 0")
        void khoang_cach_toi_thieu_mot_ngay() {
            // Hệ số dễ thấp nhất + khoảng cách nhỏ nhất: round(1 × 1.3) vẫn phải >= 1
            Sm2Calculator.Sm2Result r =
                    calculator.apply(1.3d, 1, 5, 0, Rating.HARD, NOW);
            assertThat(r.intervalDays()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Hàm là hàm thuần: gọi hai lần với cùng đầu vào cho cùng kết quả")
        void la_ham_thuan() {
            Sm2Calculator.Sm2Result a = calculator.apply(2.5d, 6, 2, 0, Rating.GOOD, NOW);
            Sm2Calculator.Sm2Result b = calculator.apply(2.5d, 6, 2, 0, Rating.GOOD, NOW);
            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("Từ chối tham số null thay vì âm thầm tính sai")
        void tu_choi_tham_so_null() {
            assertThatThrownBy(() -> calculator.apply(2.5d, 0, 0, 0, null, NOW))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> calculator.apply(2.5d, 0, 0, 0, Rating.GOOD, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Mọi mức đánh giá đều trả về thời điểm đến hạn ở tương lai")
        void moi_muc_danh_gia_deu_hen_tuong_lai() {
            for (Rating rating : Rating.values()) {
                Sm2Calculator.Sm2Result r =
                        calculator.apply(2.5d, 3, 2, 0, rating, NOW);
                assertThat(r.dueAt())
                        .as("dueAt của %s phải sau thời điểm hiện tại", rating)
                        .isAfter(NOW);
            }
        }
    }
}
