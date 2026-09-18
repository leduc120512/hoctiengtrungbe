package com.example.bewebtiengtrung.module.dictionary.service;

import com.example.bewebtiengtrung.module.vocabulary.repository.WordRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Nạp file CC-CEDICT thật (gzip trong resources) một lần cho cả lớp test và kiểm tra các phép tra.
 * Bảng words được giả lập rỗng nên tiêu chí "có trong hệ thống" không ảnh hưởng xếp hạng ở đây.
 */
class CedictServiceImplTest {

    private static final String XUEXI = "学习";   // 学习
    private static final String HAO = "好";           // 好 (đa âm: hao3 / hao4)
    private static final String XUEXIAO = "学校"; // 学校

    private static CedictServiceImpl service;

    @BeforeAll
    static void loadOnce() {
        WordRepository repo = mock(WordRepository.class);
        when(repo.findAll()).thenReturn(List.of());
        service = new CedictServiceImpl(repo);
        service.load();
    }

    @Test
    void nap_du_so_muc() {
        assertThat(service.size()).isGreaterThan(100_000);
    }

    @Test
    void tra_theo_gian_the_tra_dung_pinyin_va_nghia() {
        List<CedictEntry> found = service.lookupSimplified(XUEXI);
        assertThat(found).isNotEmpty();
        CedictEntry first = found.get(0);
        assertThat(first.pinyinNumbered()).isEqualTo("xue2 xi2");
        assertThat(first.pinyinMarked()).isEqualTo("xuéxí");
        assertThat(first.definitions()).anyMatch(d -> d.contains("study") || d.contains("learn"));
    }

    @Test
    void tu_da_am_tra_ve_nhieu_muc() {
        List<CedictEntry> found = service.lookupSimplified(HAO);
        assertThat(found.stream().map(CedictEntry::pinyinNumbered).toList())
                .contains("hao3", "hao4");
    }

    @Test
    void tra_nguoc_theo_pinyin_bat_ky_dang() {
        assertThat(service.reverseLookup("xue2 xi2", 5)).anyMatch(e -> e.simplified().equals(XUEXI));
        assertThat(service.reverseLookup("xuéxí", 5)).anyMatch(e -> e.simplified().equals(XUEXI));
        assertThat(service.reverseLookup("khong-phai-pinyin", 5)).isEmpty();
    }

    @Test
    void tim_theo_tieng_Anh_ra_chu_Han() {
        List<CedictEntry> found = service.search("school", 20);
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(e -> e.simplified().equals(XUEXIAO));
        // Mục có nghĩa bằng đúng "school" phải đứng trước mục chỉ chứa "school".
        assertThat(found.get(0).definitions().get(0).toLowerCase()).contains("school");
    }

    @Test
    void tim_tieng_Anh_uu_tien_nghia_chinh_va_tu_thong_dung() {
        // "library": 图书馆 (nghĩa đầu = library) phải đứng trước 库 (library là nghĩa thứ 3)
        // và trước 圕 (dạng rút gọn hiếm, "contracted form of ...").
        List<CedictEntry> found = service.search("library", 5);
        assertThat(found.get(0).simplified()).isEqualTo("图书馆");
    }

    @Test
    void tim_cum_tieng_Anh_hai_tu() {
        List<CedictEntry> found = service.search("go home", 10);
        assertThat(found).isNotEmpty();
        assertThat(found).allMatch(e -> e.definitions().stream()
                .anyMatch(d -> d.toLowerCase().contains("home")));
    }

    @Test
    void tim_theo_chu_Han_tien_to() {
        List<CedictEntry> found = service.search(XUEXI.substring(0, 1), 10); // 学
        assertThat(found).isNotEmpty();
        assertThat(found).allMatch(e -> e.simplified().startsWith(XUEXI.substring(0, 1))
                || e.traditional().startsWith(XUEXI.substring(0, 1)));
    }

    @Test
    void goi_y_tieng_Anh_bo_CL_va_gioi_han_3_nghia() {
        CedictEntry entry = new CedictEntry("t", "s", "a1", "ā",
                List.of("first", "CL:个", "second sense CL:个|個", "third", "fourth"));
        assertThat(service.suggestEnglish(entry)).isEqualTo("first; second sense; third");
        assertThat(service.suggestEnglish(null)).isNull();
    }
}
