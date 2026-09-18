package com.example.bewebtiengtrung.module.srs.entity;

/**
 * Trạng thái ôn tập của một thẻ đối với một người dùng (theo thuật toán SM-2).
 *
 * <ul>
 *   <li>{@code NEW}        – thẻ mới, người dùng chưa ôn lần nào.</li>
 *   <li>{@code LEARNING}   – thẻ đang trong giai đoạn học lần đầu.</li>
 *   <li>{@code REVIEW}     – thẻ đã thuộc, đang ôn theo chu kỳ giãn cách.</li>
 *   <li>{@code RELEARNING} – thẻ vừa bị quên (đánh giá AGAIN), phải học lại.</li>
 * </ul>
 */
public enum ReviewStatus {
    NEW,
    LEARNING,
    REVIEW,
    RELEARNING
}
