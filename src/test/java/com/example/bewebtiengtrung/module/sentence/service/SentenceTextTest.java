package com.example.bewebtiengtrung.module.sentence.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kiểm thử tiện ích {@link SentenceText} — bộ lọc "mọi chữ Hán trong câu phải nằm trong vốn từ đã học".
 */
class SentenceTextTest {

    /** Vốn từ mẫu: mỗi từ tách ra từng chữ khi tính tập đã học. */
    private static final List<String> LEARNED = List.of("我", "你", "他", "是", "学生", "老师", "好", "吗", "不");

    @Nested
    @DisplayName("key — khoá chống trùng")
    class Key {

        @Test
        @DisplayName("Bỏ dấu câu tiếng Trung và khoảng trắng, chỉ giữ chữ Hán")
        void bo_dau_cau_trung() {
            assertThat(SentenceText.key("你 好 吗？")).isEqualTo("你好吗");
            assertThat(SentenceText.key("我是学生。")).isEqualTo("我是学生");
            assertThat(SentenceText.key("他不是老师，我是。")).isEqualTo("他不是老师我是");
            assertThat(SentenceText.key("好！好、好")).isEqualTo("好好好");
        }

        @Test
        @DisplayName("Bỏ dấu câu kiểu Việt/Latin, chữ số và chữ latin")
        void bo_dau_cau_viet_so_latin() {
            assertThat(SentenceText.key("你好吗?")).isEqualTo("你好吗");
            assertThat(SentenceText.key("我是学生. (level 1)")).isEqualTo("我是学生");
            assertThat(SentenceText.key("3 个人 abc")).isEqualTo("个人");
        }

        @Test
        @DisplayName("Câu cùng chữ nhưng khác dấu câu cho cùng một khoá")
        void cung_chu_khac_dau_cau_cung_khoa() {
            assertThat(SentenceText.key("你好吗？")).isEqualTo(SentenceText.key("你好吗?"))
                    .isEqualTo(SentenceText.key("你 好 吗"));
        }

        @Test
        @DisplayName("null / rỗng / không có chữ Hán ⇒ chuỗi rỗng")
        void null_rong_khong_han() {
            assertThat(SentenceText.key(null)).isEmpty();
            assertThat(SentenceText.key("")).isEmpty();
            assertThat(SentenceText.key("hello, world!")).isEmpty();
            assertThat(SentenceText.key("？。，！")).isEmpty();
        }

        @Test
        @DisplayName("Chữ trong khối mở rộng A (U+3400–U+4DBF) cũng được giữ")
        void khoi_mo_rong_a() {
            String extA = new String(Character.toChars(0x3400));
            assertThat(SentenceText.key("我" + extA + "。")).isEqualTo("我" + extA);
        }
    }

    @Nested
    @DisplayName("learnedCodePoints — tập chữ đã học")
    class LearnedCodePoints {

        @Test
        @DisplayName("Tách từng chữ của từ nhiều âm tiết")
        void tach_tung_chu() {
            Set<Integer> learned = SentenceText.learnedCodePoints(List.of("学生", "老师"));
            assertThat(learned).containsExactlyInAnyOrder(
                    (int) '学', (int) '生', (int) '老', (int) '师');
        }

        @Test
        @DisplayName("Bỏ qua phần tử null/rỗng và ký tự không phải CJK trong từ")
        void bo_qua_null_rong_khong_cjk() {
            Set<Integer> learned = SentenceText.learnedCodePoints(Arrays.asList("你好", null, "", "AB 12", "吗？"));
            assertThat(learned).containsExactlyInAnyOrder((int) '你', (int) '好', (int) '吗');
        }

        @Test
        @DisplayName("Danh sách null ⇒ tập rỗng (không văng lỗi)")
        void danh_sach_null() {
            assertThat(SentenceText.learnedCodePoints(null)).isEmpty();
        }
    }

    @Nested
    @DisplayName("unknownChars — chữ lạ trong câu")
    class UnknownChars {

        private final Set<Integer> learned = SentenceText.learnedCodePoints(LEARNED);

        @Test
        @DisplayName("Câu chỉ dùng chữ đã học ⇒ hợp lệ (rỗng), dấu câu Trung bị bỏ qua")
        void cau_hop_le_dau_cau_trung() {
            assertThat(SentenceText.unknownChars("你好吗？", learned)).isEmpty();
            assertThat(SentenceText.unknownChars("我是学生，他是老师。", learned)).isEmpty();
            assertThat(SentenceText.unknownChars("他不是学生！", learned)).isEmpty();
        }

        @Test
        @DisplayName("Dấu câu kiểu Việt, khoảng trắng, số, latin cũng bị bỏ qua")
        void dau_cau_viet_bo_qua() {
            assertThat(SentenceText.unknownChars("你 好 吗?", learned)).isEmpty();
            assertThat(SentenceText.unknownChars("我是学生. (1)", learned)).isEmpty();
        }

        @Test
        @DisplayName("Chữ ngoài vốn từ được liệt kê theo thứ tự xuất hiện, không trùng lặp")
        void chu_la_theo_thu_tu_khong_trung() {
            assertThat(SentenceText.unknownChars("他很高兴。", learned)).containsExactly("很", "高", "兴");
            assertThat(SentenceText.unknownChars("我的书是你的书。", learned)).containsExactly("的", "书");
        }

        @Test
        @DisplayName("Tập đã học rỗng/null ⇒ mọi chữ Hán đều lạ")
        void tap_da_hoc_rong() {
            assertThat(SentenceText.unknownChars("你好", Set.of())).containsExactly("你", "好");
            assertThat(SentenceText.unknownChars("你好", null)).containsExactly("你", "好");
        }

        @Test
        @DisplayName("Câu null/rỗng ⇒ không có chữ lạ")
        void cau_null_rong() {
            assertThat(SentenceText.unknownChars(null, learned)).isEmpty();
            assertThat(SentenceText.unknownChars("", learned)).isEmpty();
        }
    }
}
