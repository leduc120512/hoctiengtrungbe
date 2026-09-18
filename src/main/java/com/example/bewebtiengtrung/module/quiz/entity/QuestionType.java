package com.example.bewebtiengtrung.module.quiz.entity;

/**
 * Loại câu hỏi trong đề kiểm tra.
 *
 * <p>Nhóm trắc nghiệm (SINGLE_CHOICE, MULTIPLE_CHOICE, LISTENING, IMAGE_CHOICE) chấm điểm
 * dựa trên cờ {@code isCorrect} của phương án được chọn.</p>
 * <p>Nhóm tự luận ngắn (FILL_BLANK, TRANSLATION) chấm điểm bằng cách so sánh chuỗi đã chuẩn hoá
 * với {@code question.correctText}.</p>
 */
public enum QuestionType {

    /** Chọn 1 đáp án đúng trong nhiều phương án. */
    SINGLE_CHOICE,

    /**
     * Chọn nhiều đáp án đúng.
     * Hạn chế hiện tại: API chỉ nhận 1 {@code selectedOptionId} cho mỗi câu hỏi
     * nên tạm thời được chấm giống SINGLE_CHOICE.
     */
    MULTIPLE_CHOICE,

    /** Điền vào chỗ trống - so sánh với correctText. */
    FILL_BLANK,

    /** Nghe hiểu - có audioUrl, chọn phương án đúng. */
    LISTENING,

    /** Dịch câu - so sánh với correctText. */
    TRANSLATION,

    /** Nhìn hình chọn từ - có imageUrl, chọn phương án đúng. */
    IMAGE_CHOICE
}
