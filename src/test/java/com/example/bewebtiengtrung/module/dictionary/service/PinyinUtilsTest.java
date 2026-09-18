package com.example.bewebtiengtrung.module.dictionary.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kiểm thử chuyển đổi pinyin — hai chiều và khoá so khớp.
 * Chuỗi có dấu viết bằng escape Unicode để file nguồn không phụ thuộc mã hoá của trình biên dịch.
 */
class PinyinUtilsTest {

    private static final String XUEXI_MARKED = "xuéxí";          // xuéxí
    private static final String NIHAO_MARKED_SP = "nǐ hǎo";       // nǐ hǎo
    private static final String NIHAO_MARKED = "nǐhǎo";           // nǐhǎo
    private static final String NU_MARKED = "nǚ";                      // nǚ
    private static final String LUE_MARKED = "lüè";               // lüè
    private static final String HAO3 = "hǎo";                          // hǎo
    private static final String HAO4 = "hào";                          // hào
    private static final String GOU3 = "gǒu";                          // gǒu (dấu trên o của "ou")
    private static final String LIU2 = "liú";                          // liú (nguyên âm cuối)

    @Nested
    @DisplayName("Số → dấu")
    class NumberedToMarked {

        @Test
        void hai_am_tiet_khong_khoang_trang() {
            assertThat(PinyinUtils.numberedToMarked("xue2 xi2", false)).isEqualTo(XUEXI_MARKED);
        }

        @Test
        void giu_khoang_trang_khi_yeu_cau() {
            assertThat(PinyinUtils.numberedToMarked("ni3 hao3", true)).isEqualTo(NIHAO_MARKED_SP);
        }

        @Test
        void u_hai_cham_thanh_u_umlaut() {
            assertThat(PinyinUtils.numberedToMarked("nu:3", false)).isEqualTo(NU_MARKED);
            assertThat(PinyinUtils.numberedToMarked("lu:e4", false)).isEqualTo(LUE_MARKED);
        }

        @Test
        void thanh_nhe_va_khong_so_thi_khong_dau() {
            assertThat(PinyinUtils.numberedToMarked("ma5", false)).isEqualTo("ma");
            assertThat(PinyinUtils.numberedToMarked("ma", false)).isEqualTo("ma");
        }

        @Test
        void quy_tac_dat_dau_ou_va_nguyen_am_cuoi() {
            assertThat(PinyinUtils.numberedToMarked("gou3", false)).isEqualTo(GOU3);
            assertThat(PinyinUtils.numberedToMarked("liu2", false)).isEqualTo(LIU2);
        }

        @Test
        void chuoi_rong_hoac_null() {
            assertThat(PinyinUtils.numberedToMarked("", false)).isEmpty();
            assertThat(PinyinUtils.numberedToMarked(null, false)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Dấu → số")
    class MarkedToNumbered {

        @Test
        void co_khoang_trang() {
            assertThat(PinyinUtils.markedToNumbered(NIHAO_MARKED_SP)).isEqualTo("ni3 hao3");
        }

        @Test
        void khong_khoang_trang_tach_am_tiet_bang_heuristic() {
            assertThat(PinyinUtils.markedToNumbered(XUEXI_MARKED)).isEqualTo("xue2 xi2");
            assertThat(PinyinUtils.markedToNumbered(NIHAO_MARKED)).isEqualTo("ni3 hao3");
        }

        @Test
        void u_umlaut_thanh_u_hai_cham() {
            assertThat(PinyinUtils.markedToNumbered(NU_MARKED)).isEqualTo("nu:3");
        }

        @Test
        void khong_dau_thanh_5() {
            assertThat(PinyinUtils.markedToNumbered("ma")).isEqualTo("ma5");
        }

        @Test
        void da_la_dang_so_thi_giu_nguyen() {
            assertThat(PinyinUtils.markedToNumbered("xue2 xi2")).isEqualTo("xue2 xi2");
            // Dạng số kiểu CC-CEDICT với ü viết là "u:" phải giữ nguyên, không bị tách ở dấu ':'.
            assertThat(PinyinUtils.markedToNumbered("nu:3")).isEqualTo("nu:3");
            assertThat(PinyinUtils.markedToNumbered("lu:e4")).isEqualTo("lu:e4");
        }
    }

    @Nested
    @DisplayName("Khoá so khớp")
    class NormalizeKey {

        @Test
        void moi_cach_viet_cung_cho_mot_khoa() {
            assertThat(PinyinUtils.normalizeKey(XUEXI_MARKED)).isEqualTo("xue2xi2");
            assertThat(PinyinUtils.normalizeKey("xue2 xi2")).isEqualTo("xue2xi2");
            assertThat(PinyinUtils.normalizeKey("Xue2Xi2")).isEqualTo("xue2xi2");
        }

        @Test
        void thanh_dieu_khac_nhau_cho_khoa_khac_nhau() {
            assertThat(PinyinUtils.normalizeKey(HAO3)).isNotEqualTo(PinyinUtils.normalizeKey(HAO4));
        }

        @Test
        void u_umlaut_va_u_hai_cham_cung_khoa() {
            assertThat(PinyinUtils.normalizeKey(NU_MARKED)).isEqualTo(PinyinUtils.normalizeKey("nu:3"));
            assertThat(PinyinUtils.normalizeKey("nv3")).isEqualTo(PinyinUtils.normalizeKey("nu:3"));
        }
    }

    @Test
    void isChinese_chi_nhan_chuoi_toan_chu_Han() {
        assertThat(PinyinUtils.isChinese("学习")).isTrue();      // 学习
        assertThat(PinyinUtils.isChinese("学 习")).isFalse();    // có khoảng trắng
        assertThat(PinyinUtils.isChinese("xuexi")).isFalse();
        assertThat(PinyinUtils.isChinese("")).isFalse();
        assertThat(PinyinUtils.isChinese(null)).isFalse();
    }
}
