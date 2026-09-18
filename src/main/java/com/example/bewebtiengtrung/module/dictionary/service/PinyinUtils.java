package com.example.bewebtiengtrung.module.dictionary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tiện ích chuyển đổi pinyin — hàm tĩnh thuần, không phụ thuộc Spring.
 *
 * <p>Hai dạng pinyin được dùng trong hệ thống:
 * <ul>
 *   <li><b>Dạng số</b> (CC-CEDICT): {@code "xue2 xi2"}, {@code "nu:3"} (ü viết là {@code u:}), thanh nhẹ = 5.</li>
 *   <li><b>Dạng dấu</b> (hiển thị): {@code "xuéxí"}, {@code "nǚ"}.</li>
 * </ul>
 *
 * <p>Quy tắc đặt dấu thanh (chuẩn Hán ngữ pinyin): nếu có {@code a} hoặc {@code e} thì đặt lên đó;
 * nếu có {@code ou} thì đặt lên {@code o}; ngược lại đặt lên nguyên âm cuối cùng.
 *
 * <p>Giới hạn: khi chuyển dạng dấu → số mà chuỗi KHÔNG có khoảng trắng (ví dụ {@code "xuéxí"}),
 * việc tách âm tiết dùng heuristic nguyên âm/phụ âm; đủ đúng cho từ vựng HSK nhưng có thể sai với
 * các tổ hợp hiếm. Với chuỗi có khoảng trắng thì tách chính xác.
 */
public final class PinyinUtils {

    private PinyinUtils() {
    }

    /** Bảng dấu thanh cho từng nguyên âm: [không dấu, thanh 1, 2, 3, 4]. */
    private static final Map<Character, char[]> TONE_MARKS = Map.of(
            'a', new char[]{'a', 'ā', 'á', 'ǎ', 'à'},
            'e', new char[]{'e', 'ē', 'é', 'ě', 'è'},
            'i', new char[]{'i', 'ī', 'í', 'ǐ', 'ì'},
            'o', new char[]{'o', 'ō', 'ó', 'ǒ', 'ò'},
            'u', new char[]{'u', 'ū', 'ú', 'ǔ', 'ù'},
            'ü', new char[]{'ü', 'ǖ', 'ǘ', 'ǚ', 'ǜ'}
    );

    /** Tra ngược: ký tự có dấu → (nguyên âm gốc, thanh). Dựng từ TONE_MARKS lúc nạp lớp. */
    private static final Map<Character, int[]> MARK_TO_BASE = buildMarkToBase();

    private static Map<Character, int[]> buildMarkToBase() {
        Map<Character, int[]> map = new java.util.HashMap<>();
        for (Map.Entry<Character, char[]> e : TONE_MARKS.entrySet()) {
            char[] row = e.getValue();
            for (int tone = 1; tone <= 4; tone++) {
                map.put(row[tone], new int[]{e.getKey(), tone});
            }
        }
        return map;
    }

    private static boolean isVowel(char c) {
        return "aeiouü".indexOf(c) >= 0 || MARK_TO_BASE.containsKey(c);
    }

    // ------------------------------------------------------------------ số → dấu

    /**
     * {@code "xue2 xi2"} → {@code "xuéxí"} ({@code keepSpaces=false}) hoặc {@code "xué xí"} ({@code true}).
     * {@code "nu:3"} → {@code "nǚ"}; {@code "lu:e4"} → {@code "lüè"}; thanh 5 hoặc không có số → không dấu.
     */
    public static String numberedToMarked(String numbered, boolean keepSpaces) {
        if (numbered == null || numbered.isBlank()) {
            return "";
        }
        String[] syllables = numbered.trim().split("\\s+");
        List<String> out = new ArrayList<>(syllables.length);
        for (String syllable : syllables) {
            out.add(markSyllable(syllable));
        }
        return String.join(keepSpaces ? " " : "", out);
    }

    private static String markSyllable(String raw) {
        String s = raw.toLowerCase(Locale.ROOT).replace("u:", "ü").replace('v', 'ü');
        int tone = 5;
        int last = s.length() - 1;
        if (last >= 0 && Character.isDigit(s.charAt(last))) {
            tone = s.charAt(last) - '0';
            s = s.substring(0, last);
        }
        if (tone < 1 || tone > 4) {
            return s;
        }
        int idx = toneIndex(s);
        if (idx < 0) {
            return s;
        }
        char base = s.charAt(idx);
        char[] row = TONE_MARKS.get(base);
        if (row == null) {
            return s;
        }
        return s.substring(0, idx) + row[tone] + s.substring(idx + 1);
    }

    /** Vị trí nguyên âm nhận dấu theo quy tắc a/e → ou → nguyên âm cuối. */
    private static int toneIndex(String s) {
        int a = s.indexOf('a');
        if (a >= 0) {
            return a;
        }
        int e = s.indexOf('e');
        if (e >= 0) {
            return e;
        }
        int ou = s.indexOf("ou");
        if (ou >= 0) {
            return ou;
        }
        for (int i = s.length() - 1; i >= 0; i--) {
            if (isVowel(s.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    // ------------------------------------------------------------------ dấu → số

    /**
     * {@code "xuéxí"} → {@code "xue2 xi2"}; {@code "nǐ hǎo"} → {@code "ni3 hao3"}; {@code "nǚ"} → {@code "nu:3"}.
     * Âm tiết không dấu → thanh 5. Chuỗi đã ở dạng số được trả về sau khi chuẩn hoá khoảng trắng.
     */
    public static String markedToNumbered(String marked) {
        if (marked == null || marked.isBlank()) {
            return "";
        }
        List<String> syllables = new ArrayList<>();
        for (String chunk : marked.trim().split("\\s+")) {
            syllables.addAll(splitSyllables(chunk));
        }
        List<String> out = new ArrayList<>(syllables.size());
        for (String syllable : syllables) {
            out.add(numberSyllable(syllable));
        }
        return String.join(" ", out);
    }

    private static String numberSyllable(String syllable) {
        StringBuilder plain = new StringBuilder();
        int tone = 5;
        String s = syllable.toLowerCase(Locale.ROOT);
        // Đã có số thanh ở cuối thì giữ nguyên (chỉ chuẩn hoá ü).
        char lastChar = s.charAt(s.length() - 1);
        if (Character.isDigit(lastChar)) {
            return s.substring(0, s.length() - 1).replace("ü", "u:") + lastChar;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int[] base = MARK_TO_BASE.get(c);
            if (base != null) {
                plain.append((char) base[0]);
                tone = base[1];
            } else {
                plain.append(c);
            }
        }
        return plain.toString().replace("ü", "u:") + tone;
    }

    /**
     * Tách một cụm không có khoảng trắng thành các âm tiết. Heuristic: mỗi âm tiết có một cụm nguyên
     * âm; một phụ âm đứng sau nguyên âm mở âm tiết mới, trừ {@code n}/{@code ng}/{@code r} kết âm khi
     * ngay sau nó không phải nguyên âm.
     */
    static List<String> splitSyllables(String chunk) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        String s = chunk.replace('\'', ' ').trim();
        // Dấu nháy (xi'an) là ranh giới rõ ràng.
        if (s.indexOf(' ') >= 0) {
            for (String part : s.split("\\s+")) {
                out.addAll(splitSyllables(part));
            }
            return out;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // Dấu ':' là ký hiệu ü của CC-CEDICT (nu:3) — thuộc về nguyên âm đứng trước, không mở âm tiết mới.
            if (c == ':') {
                cur.append(c);
                continue;
            }
            // Chữ số thanh điệu (xue2xi2) kết thúc âm tiết hiện tại ngay lập tức.
            if (Character.isDigit(c)) {
                cur.append(c);
                out.add(cur.toString());
                cur.setLength(0);
                continue;
            }
            boolean isV = isVowel(c);
            char prev = cur.length() > 0 ? cur.charAt(cur.length() - 1) : 0;
            boolean prevIsV = prev != 0 && isVowel(prev);
            boolean nextIsV = i + 1 < s.length() && isVowel(s.charAt(i + 1));
            if (cur.length() > 0 && !isV && (prevIsV || prev == 'n' || prev == 'g' || prev == 'r')) {
                if ((c == 'n' || c == 'r') && !nextIsV) {
                    cur.append(c);
                    continue;
                }
                if (c == 'g' && prev == 'n' && !nextIsV) {
                    cur.append(c);
                    continue;
                }
                out.add(cur.toString());
                cur.setLength(0);
                cur.append(c);
                continue;
            }
            cur.append(c);
        }
        if (cur.length() > 0) {
            out.add(cur.toString());
        }
        return out;
    }

    // ------------------------------------------------------------------ khoá so khớp

    /**
     * Khoá so khớp không phụ thuộc cách viết: chữ thường, bỏ khoảng trắng, dấu → số, ü/u: → v.
     * {@code "xuéxí"}, {@code "xue2 xi2"}, {@code "Xue2Xi2"} đều cho {@code "xue2xi2"}.
     */
    public static String normalizeKey(String anyPinyin) {
        if (anyPinyin == null) {
            return "";
        }
        String numbered = markedToNumbered(anyPinyin.replace("u:", "ü").replace('v', 'ü'));
        return numbered.toLowerCase(Locale.ROOT)
                .replace("u:", "v")
                .replace("ü", "v")
                .replaceAll("\\s+", "");
    }

    /** {@code true} nếu chuỗi không rỗng và chỉ gồm ký tự CJK (U+4E00–U+9FFF, U+3400–U+4DBF). */
    public static boolean isChinese(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.codePoints().allMatch(PinyinUtils::isCjk);
    }

    /** Một code point có phải chữ Hán không (dùng chung cho các module khác). */
    public static boolean isCjk(int cp) {
        return (cp >= 0x4E00 && cp <= 0x9FFF) || (cp >= 0x3400 && cp <= 0x4DBF);
    }
}
