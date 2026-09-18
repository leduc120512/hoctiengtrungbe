package com.example.bewebtiengtrung.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Tiện ích sinh "slug" (chuỗi ASCII dùng trên URL) từ tiêu đề tiếng Việt hoặc tiếng Trung.
 *
 * <p>Quy tắc xử lý:</p>
 * <ol>
 *   <li>Chuyển về chữ thường;</li>
 *   <li>Thay đ/Đ thành d (ký tự này không tách được dấu bằng Unicode NFD);</li>
 *   <li>Tách dấu tiếng Việt bằng chuẩn hoá NFD rồi loại bỏ các dấu thanh/dấu mũ;</li>
 *   <li>Mọi ký tự không thuộc [a-z0-9] (kể cả Hán tự) trở thành dấu gạch ngang;</li>
 *   <li>Gộp các dấu gạch liên tiếp, cắt gạch ở hai đầu và giới hạn độ dài.</li>
 * </ol>
 *
 * <p>Ví dụ: {@code "Chào bạn - 你好 HSK 1"} → {@code "chao-ban-hsk-1"}.</p>
 *
 * <p>Nếu chuỗi đầu vào chỉ gồm Hán tự (bị loại hết), phương thức trả về một slug dự phòng
 * dạng {@code "muc-<mã hash>"} để không bao giờ sinh ra slug rỗng.</p>
 */
public final class SlugUtils {

    /** Độ dài tối đa mặc định — vừa với cột slug VARCHAR(150) trong CSDL. */
    public static final int DEFAULT_MAX_LENGTH = 150;

    /** Tiền tố cho slug dự phòng khi tiêu đề không còn ký tự ASCII nào. */
    private static final String FALLBACK_PREFIX = "muc-";

    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final Pattern EDGE_HYPHENS = Pattern.compile("(^-+)|(-+$)");

    private SlugUtils() {
        throw new UnsupportedOperationException("Lớp tiện ích, không cho phép khởi tạo");
    }

    /**
     * Sinh slug từ tiêu đề với độ dài tối đa mặc định ({@value #DEFAULT_MAX_LENGTH} ký tự).
     *
     * @param input tiêu đề gốc (có thể null)
     * @return slug ASCII, không bao giờ null và không bao giờ rỗng
     */
    public static String slugify(String input) {
        return slugify(input, DEFAULT_MAX_LENGTH);
    }

    /**
     * Sinh slug từ tiêu đề, giới hạn ở {@code maxLength} ký tự.
     *
     * @param input     tiêu đề gốc (có thể null)
     * @param maxLength độ dài tối đa (phải lớn hơn 0)
     * @return slug ASCII, không bao giờ null và không bao giờ rỗng
     */
    public static String slugify(String input, int maxLength) {
        int limit = maxLength > 0 ? maxLength : DEFAULT_MAX_LENGTH;
        if (input == null || input.isBlank()) {
            return FALLBACK_PREFIX + "0";
        }

        String lower = input.trim().toLowerCase(Locale.ROOT);
        // Chữ đ/Đ của tiếng Việt không tách được dấu nên phải thay thủ công
        String withoutD = lower.replace('đ', 'd').replace('Đ', 'd');
        String decomposed = Normalizer.normalize(withoutD, Normalizer.Form.NFD);
        String withoutMarks = COMBINING_MARKS.matcher(decomposed).replaceAll("");
        String hyphenated = NON_ALPHANUMERIC.matcher(withoutMarks).replaceAll("-");
        String trimmed = EDGE_HYPHENS.matcher(hyphenated).replaceAll("");

        if (trimmed.length() > limit) {
            trimmed = trimmed.substring(0, limit);
            trimmed = EDGE_HYPHENS.matcher(trimmed).replaceAll("");
        }

        if (trimmed.isEmpty()) {
            // Trường hợp tiêu đề thuần Hán tự: dùng hash để vẫn có slug ổn định, duy nhất tương đối
            return FALLBACK_PREFIX + Integer.toHexString(input.trim().hashCode() & 0x7fffffff);
        }
        return trimmed;
    }

    /** Bí danh của {@link #slugify(String)} cho quen tay khi gọi từ các module khác. */
    public static String toSlug(String input) {
        return slugify(input);
    }

    /**
     * Sinh slug duy nhất: nếu slug gốc đã tồn tại thì nối thêm hậu tố {@code -2}, {@code -3}...
     *
     * @param input  tiêu đề gốc
     * @param exists hàm kiểm tra slug đã tồn tại trong CSDL hay chưa
     * @return slug chưa được sử dụng
     */
    public static String uniqueSlug(String input, Predicate<String> exists) {
        String base = slugify(input);
        if (exists == null || !exists.test(base)) {
            return base;
        }
        for (int counter = 2; counter < 1000; counter++) {
            String suffix = "-" + counter;
            String candidate = base.length() + suffix.length() > DEFAULT_MAX_LENGTH
                    ? base.substring(0, DEFAULT_MAX_LENGTH - suffix.length()) + suffix
                    : base + suffix;
            if (!exists.test(candidate)) {
                return candidate;
            }
        }
        // Rất hiếm khi xảy ra: quay về hậu tố thời gian để đảm bảo luôn có kết quả
        String suffix = "-" + Long.toString(System.currentTimeMillis(), 36);
        String prefix = base.length() + suffix.length() > DEFAULT_MAX_LENGTH
                ? base.substring(0, DEFAULT_MAX_LENGTH - suffix.length())
                : base;
        return prefix + suffix;
    }
}
