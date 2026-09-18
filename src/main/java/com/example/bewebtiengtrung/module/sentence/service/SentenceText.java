package com.example.bewebtiengtrung.module.sentence.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Tiện ích tĩnh thuần xử lý chữ Hán trong câu — không phụ thuộc Spring, có unit test riêng.
 *
 * <p>Quy tắc "mọi chữ Hán trong câu phải nằm trong vốn từ đã học" là ràng buộc chất lượng số 1
 * của tính năng "Câu của tôi": câu do AI sinh ra cũng phải qua đúng bộ lọc này.</p>
 *
 * <p>Ký tự CJK được nhận diện gồm khối thống nhất U+4E00–U+9FFF và khối mở rộng A U+3400–U+4DBF.
 * Mọi thứ khác (dấu câu Trung/Việt, khoảng trắng, chữ số, latin) đều bị bỏ qua.</p>
 */
public final class SentenceText {

    private SentenceText() {
        throw new UnsupportedOperationException("Lớp tiện ích, không cho phép khởi tạo");
    }

    /** Ký tự có phải chữ Hán (CJK) hay không. */
    public static boolean isCjk(int codePoint) {
        return (codePoint >= 0x4E00 && codePoint <= 0x9FFF)
                || (codePoint >= 0x3400 && codePoint <= 0x4DBF);
    }

    /**
     * Khoá chống trùng của câu: chỉ giữ ký tự CJK, bỏ dấu câu, khoảng trắng, số, latin.
     *
     * @return chuỗi chỉ gồm chữ Hán; rỗng nếu {@code hanzi} null hoặc không có chữ Hán nào
     */
    public static String key(String hanzi) {
        if (hanzi == null || hanzi.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(hanzi.length());
        hanzi.codePoints().filter(SentenceText::isCjk).forEach(sb::appendCodePoint);
        return sb.toString();
    }

    /**
     * Tập ký tự CJK xuất hiện trong danh sách từ đã học (mỗi từ tách ra từng chữ).
     *
     * @param learnedWords các từ đã học (ví dụ "学习", "你好"); phần tử null/rỗng bị bỏ qua
     * @return tập code point (có thể thay đổi) của mọi chữ Hán trong các từ đó
     */
    public static Set<Integer> learnedCodePoints(Collection<String> learnedWords) {
        Set<Integer> result = new HashSet<>();
        if (learnedWords == null) {
            return result;
        }
        for (String word : learnedWords) {
            if (word == null || word.isEmpty()) {
                continue;
            }
            word.codePoints().filter(SentenceText::isCjk).forEach(result::add);
        }
        return result;
    }

    /**
     * Các chữ trong câu KHÔNG nằm trong tập đã học — rỗng nghĩa là câu hợp lệ.
     *
     * <p>Dấu câu (Trung lẫn Việt), khoảng trắng, số, latin đều bị bỏ qua. Mỗi chữ lạ chỉ liệt kê
     * một lần, theo thứ tự xuất hiện.</p>
     *
     * @param hanzi   câu chữ Hán
     * @param learned tập code point đã học (xem {@link #learnedCodePoints})
     * @return danh sách chữ lạ (mỗi phần tử là một chữ), không trùng lặp
     */
    public static List<String> unknownChars(String hanzi, Set<Integer> learned) {
        if (hanzi == null || hanzi.isEmpty()) {
            return List.of();
        }
        Set<Integer> known = learned == null ? Set.of() : learned;
        Set<Integer> unknown = new LinkedHashSet<>();
        hanzi.codePoints()
                .filter(SentenceText::isCjk)
                .filter(cp -> !known.contains(cp))
                .forEach(unknown::add);
        List<String> result = new ArrayList<>(unknown.size());
        for (int cp : unknown) {
            result.add(new String(Character.toChars(cp)));
        }
        return result;
    }
}
