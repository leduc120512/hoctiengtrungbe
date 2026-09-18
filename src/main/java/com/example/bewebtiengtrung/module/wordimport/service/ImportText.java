package com.example.bewebtiengtrung.module.wordimport.service;

import com.example.bewebtiengtrung.module.dictionary.service.PinyinUtils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Tiện ích chuẩn hoá chuỗi dùng chung cho preview và confirm của module nhập từ vựng.
 *
 * <p>Mọi pinyin đi qua đây đều được đưa về <b>dạng có dấu</b> trước khi so khớp hay lưu xuống
 * cột {@code words.pinyin}, nhờ vậy người dùng gõ {@code "xue2 xi2"}, {@code "Xue2Xi2"} hay
 * {@code "xuéxí"} đều cho cùng một kết quả. Lớp này không phụ thuộc Spring.</p>
 */
final class ImportText {

    /** Pinyin dạng đánh số: có ít nhất một chữ cái (hoặc dấu ":" của ü) theo sau bởi số thanh 1..5. */
    private static final Pattern NUMBERED_PINYIN = Pattern.compile(".*[A-Za-z:][1-5].*");

    /** Số thanh theo ngay sau là chữ cái ⇒ hai âm tiết bị viết dính nhau ("Xue2Xi2"). */
    private static final Pattern GLUED_SYLLABLES = Pattern.compile("([1-5])(?=[A-Za-z])");

    private ImportText() {
    }

    /**
     * Chuẩn hoá Unicode về NFC (dấu thanh gõ dạng tổ hợp vẫn so khớp được), trim, chuỗi rỗng coi là null.
     */
    static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = Normalizer.normalize(value, Normalizer.Form.NFC).trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /** {@code true} nếu chuỗi được gõ ở dạng đánh số thanh điệu ({@code "ni3 hao3"}, {@code "nu:3"}). */
    static boolean isNumberedPinyin(String pinyin) {
        return pinyin != null && NUMBERED_PINYIN.matcher(pinyin).matches();
    }

    /**
     * Đưa pinyin về dạng có dấu. Dạng đánh số được tách âm tiết (kể cả khi viết dính nhau) rồi chuyển
     * bằng {@link PinyinUtils#numberedToMarked}; dạng có dấu (hoặc không có thanh) giữ nguyên.
     * Trả về {@code null} khi đầu vào null.
     */
    static String toMarkedPinyin(String pinyin) {
        if (pinyin == null) {
            return null;
        }
        if (!isNumberedPinyin(pinyin)) {
            return pinyin;
        }
        String spaced = GLUED_SYLLABLES.matcher(pinyin).replaceAll("$1 ");
        String marked = PinyinUtils.numberedToMarked(spaced, true);
        return marked.isEmpty() ? pinyin : marked;
    }

    /**
     * Khoá so khớp pinyin không phụ thuộc cách viết (đi qua {@link #toMarkedPinyin} trước rồi
     * {@link PinyinUtils#normalizeKey}). {@code null} cho chuỗi rỗng.
     */
    static String pinyinKey(String pinyin) {
        return PinyinUtils.normalizeKey(toMarkedPinyin(pinyin));
    }
}
