package com.example.bewebtiengtrung.module.srs.entity;

/**
 * Mức đánh giá của người dùng sau khi xem một thẻ.
 * Mỗi mức tương ứng với một giá trị "quality" (q) trong công thức SM-2:
 * q &lt; 3 nghĩa là đã quên và phải học lại từ đầu.
 */
public enum Rating {

    /** Quên hoàn toàn – q = 0. */
    AGAIN(0),

    /** Nhớ nhưng rất khó – q = 3. */
    HARD(3),

    /** Nhớ bình thường – q = 4. */
    GOOD(4),

    /** Nhớ rất dễ – q = 5. */
    EASY(5);

    private final int quality;

    Rating(int quality) {
        this.quality = quality;
    }

    /** Giá trị q dùng trong công thức SM-2. */
    public int getQuality() {
        return quality;
    }
}
