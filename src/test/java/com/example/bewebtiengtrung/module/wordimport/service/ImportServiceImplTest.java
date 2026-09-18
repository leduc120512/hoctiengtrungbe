package com.example.bewebtiengtrung.module.wordimport.service;

import com.example.bewebtiengtrung.common.exception.ConflictException;
import com.example.bewebtiengtrung.module.dictionary.service.CedictEntry;
import com.example.bewebtiengtrung.module.dictionary.service.CedictService;
import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import com.example.bewebtiengtrung.module.userword.service.UserWordService;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportAction;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmRequest;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmResponse;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmRow;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewRequest;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewResponse;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewRow;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportRowInput;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportRowStatus;
import com.example.bewebtiengtrung.module.wordimport.repository.ImportWordLookupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Kiểm thử logic duyệt trước và xác nhận nhập từ vựng với từ điển, sổ từ và repository giả.
 *
 * <p>{@code PinyinUtils} là lớp tĩnh thuần nên dùng bản thật: các so khớp thanh điệu dưới đây
 * dựa vào {@code normalizeKey} thật để bảo đảm bài test phản ánh đúng hành vi khi chạy.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImportServiceImplTest {

    private static final Long USER_ID = 1L;

    @Mock
    private CedictService cedictService;

    @Mock
    private UserWordService userWordService;

    @Mock
    private ImportWordLookupRepository lookupRepository;

    @Mock
    private ImportRowExecutor rowExecutor;

    private ImportServiceImpl service;

    /** Mục từ điển cho 学习 [xue2 xi2]. */
    private static final CedictEntry XUEXI = new CedictEntry("學習", "学习", "xue2 xi2", "xuéxí",
            List.of("to learn", "to study"));

    /** Mục từ điển cho 你好 [ni3 hao3]. */
    private static final CedictEntry NIHAO = new CedictEntry("你好", "你好", "ni3 hao3", "nǐ hǎo",
            List.of("hello", "hi"));

    @BeforeEach
    void setUp() {
        service = new ImportServiceImpl(cedictService, userWordService, lookupRepository, rowExecutor);
        // Mặc định: từ điển trống, hệ thống trống, người dùng chưa học gì
        when(cedictService.lookupSimplified(anyString())).thenReturn(List.of());
        when(cedictService.reverseLookup(anyString(), anyInt())).thenReturn(List.of());
        when(cedictService.suggestEnglish(any())).thenAnswer(inv -> {
            CedictEntry e = inv.getArgument(0);
            return String.join("; ", e.definitions());
        });
        when(lookupRepository.findBySimplified(anyString())).thenReturn(List.of());
        when(lookupRepository.existsBySimplified(anyString())).thenReturn(false);
        when(userWordService.learnedWordIds(USER_ID)).thenReturn(List.of());
    }

    private static ImportRowInput row(String simplified, String pinyin, String meaningVi) {
        return new ImportRowInput(simplified, pinyin, meaningVi, null);
    }

    private static ImportPreviewRequest request(ImportRowInput... rows) {
        return new ImportPreviewRequest(1, null, List.of(rows));
    }

    private static Word word(long id, String simplified, String pinyin, String meaningVi) {
        Word w = Word.builder()
                .simplified(simplified)
                .pinyin(pinyin)
                .pinyinNumbered(null)
                .meaningVi(meaningVi)
                .meaningEn(null)
                .hskLevel(1)
                .build();
        w.setId(id);
        return w;
    }

    @Nested
    @DisplayName("Preview — thiếu chữ Hán")
    class ThieuChuHan {

        @Test
        @DisplayName("Chỉ có pinyin thì tra ngược ra gợi ý, WARNING + NEEDS_INPUT")
        void thieu_chu_han_thi_goi_y_candidates() {
            when(cedictService.reverseLookup("xuéxí", 8)).thenReturn(List.of(XUEXI));
            when(lookupRepository.existsBySimplified("学习")).thenReturn(true);

            ImportPreviewResponse res = service.preview(USER_ID, request(row(null, "xuéxí", "học")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.WARNING);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.NEEDS_INPUT);
            assertThat(r.simplified()).isNull();
            assertThat(r.candidates()).hasSize(1);
            assertThat(r.candidates().get(0).simplified()).isEqualTo("学习");
            assertThat(r.candidates().get(0).traditional()).isEqualTo("學習");
            assertThat(r.candidates().get(0).pinyinMarked()).isEqualTo("xuéxí");
            assertThat(r.candidates().get(0).meaningEn()).isEqualTo("to learn; to study");
            assertThat(r.candidates().get(0).inSystem()).isTrue();
            assertThat(r.messages()).anyMatch(m -> m.startsWith("Thiếu chữ Hán"));
            assertThat(res.summary().needsAttention()).isEqualTo(1);
            assertThat(res.summary().willCreate()).isZero();
            // Dòng dừng ở bước 2: không tra từ điển xuôi, không đối chiếu hệ thống
            verify(cedictService, never()).lookupSimplified(anyString());
            verify(lookupRepository, never()).findBySimplified(anyString());
        }

        @Test
        @DisplayName("Chữ Hán là ký tự latin cũng coi như thiếu chữ Hán")
        void chu_han_khong_hop_le_thi_coi_nhu_thieu() {
            ImportPreviewResponse res = service.preview(USER_ID, request(row("abc", "nǐ hǎo", "xin chào")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.WARNING);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.NEEDS_INPUT);
            verify(cedictService).reverseLookup("nǐ hǎo", 8);
        }

        @Test
        @DisplayName("Thiếu cả chữ Hán lẫn pinyin thì ERROR")
        void thieu_ca_hai_thi_error() {
            ImportPreviewResponse res = service.preview(USER_ID, request(row("  ", null, "học")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.ERROR);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.SKIP);
            assertThat(r.messages()).contains("Thiếu cả chữ Hán lẫn pinyin");
            assertThat(res.summary().errors()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Preview — đối chiếu từ điển")
    class DoiChieuTuDien {

        @Test
        @DisplayName("Pinyin lệch thanh điệu so với từ điển thì WARNING kèm gợi ý pinyin đúng")
        void pinyin_lech_thanh_dieu_thi_warning() {
            when(cedictService.lookupSimplified("学习")).thenReturn(List.of(XUEXI));

            ImportPreviewResponse res = service.preview(USER_ID, request(row("学习", "xuèxí", "học")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.WARNING);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.CREATE);
            assertThat(r.dictionaryFound()).isTrue();
            assertThat(r.pinyinMatchesDictionary()).isFalse();
            assertThat(r.dictionaryPinyin()).isEqualTo("xuéxí");
            // Giữ pinyin người dùng nhập, để họ tự quyết định có áp dụng gợi ý hay không
            assertThat(r.pinyin()).isEqualTo("xuèxí");
            assertThat(r.traditional()).isEqualTo("學習");
            assertThat(r.meaningEn()).isEqualTo("to learn; to study");
            assertThat(r.dictionarySenses()).hasSize(1);
            assertThat(r.messages()).anyMatch(m -> m.contains("khác từ điển") && m.contains("xuéxí"));
            assertThat(res.summary().needsAttention()).isEqualTo(1);
            assertThat(res.summary().willCreate()).isEqualTo(1);
        }

        @Test
        @DisplayName("Pinyin khớp từ điển (dù viết hoa/khoảng trắng khác) thì OK + CREATE")
        void pinyin_khop_thi_ok() {
            when(cedictService.lookupSimplified("学习")).thenReturn(List.of(XUEXI));

            ImportPreviewResponse res = service.preview(USER_ID, request(row("学习", "Xué xí", "học")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.OK);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.CREATE);
            assertThat(r.pinyinMatchesDictionary()).isTrue();
            assertThat(r.pinyin()).isEqualTo("Xué xí");
            assertThat(r.pinyinNumbered()).isNotBlank();
            assertThat(r.hskLevel()).isEqualTo(1);
            assertThat(r.messages()).isEmpty();
        }

        @Test
        @DisplayName("Pinyin gõ dạng số (kể cả viết dính 'Xue2Xi2') được đổi sang có dấu trước khi so khớp và lưu")
        void pinyin_dang_so_duoc_doi_sang_co_dau() {
            when(cedictService.lookupSimplified("学习")).thenReturn(List.of(XUEXI));

            ImportPreviewResponse res = service.preview(USER_ID, request(
                    row("学习", "Xue2Xi2", "học"),
                    row("学习", "xue2 xi2", "học")));

            ImportPreviewRow glued = res.rows().get(0);
            assertThat(glued.status()).isEqualTo(ImportRowStatus.OK);
            assertThat(glued.pinyinMatchesDictionary()).isTrue();
            assertThat(glued.pinyin()).isEqualTo("xué xí");
            assertThat(glued.pinyinNumbered()).isEqualTo("xue2 xi2");
            // Dòng hai cùng chữ + cùng cách đọc ⇒ bị bắt trùng trong lô dù viết khác
            assertThat(res.rows().get(1).status()).isEqualTo(ImportRowStatus.ERROR);
            assertThat(res.rows().get(1).messages()).contains("Trùng với dòng 1");
        }

        @Test
        @DisplayName("Pinyin dán vào ở dạng Unicode tổ hợp (NFD) vẫn khớp từ điển")
        void pinyin_nfd_van_khop() {
            when(cedictService.lookupSimplified("学习")).thenReturn(List.of(XUEXI));

            // "xuéxí" viết bằng e + dấu sắc tổ hợp (U+0301) thay vì ký tự é dựng sẵn
            ImportPreviewResponse res = service.preview(USER_ID, request(row("学习", "xuéxí", "học")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.OK);
            assertThat(r.pinyinMatchesDictionary()).isTrue();
            assertThat(r.pinyin()).isEqualTo("xuéxí");
        }

        @Test
        @DisplayName("Không có pinyin, không có trong từ điển nhưng hệ thống có đúng một từ cùng chữ thì EXISTS/LINK")
        void khong_pinyin_khong_tu_dien_nhung_he_thong_co_thi_link() {
            when(lookupRepository.findBySimplified("做什么"))
                    .thenReturn(List.of(word(9L, "做什么", "zuò shénme", "làm gì")));

            ImportPreviewResponse res = service.preview(USER_ID, request(row("做什么", null, null)));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.EXISTS);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.LINK);
            assertThat(r.pinyin()).isEqualTo("zuò shénme");
            assertThat(r.existingWord().id()).isEqualTo(9L);
            assertThat(r.messages()).contains("Đã có trong hệ thống (id 9)");
        }

        @Test
        @DisplayName("Không có pinyin từ người dùng lẫn từ điển lẫn hệ thống thì ERROR (words.pinyin NOT NULL)")
        void khong_co_pinyin_o_dau_ca_thi_error() {
            ImportPreviewResponse res = service.preview(USER_ID, request(row("做什么", null, "làm gì")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.ERROR);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.SKIP);
            assertThat(r.pinyin()).isNull();
            assertThat(r.messages()).anyMatch(m -> m.startsWith("Thiếu pinyin"));
        }

        @Test
        @DisplayName("Phần tử null trong danh sách dòng được coi là dòng trống, không ném lỗi")
        void dong_null_coi_nhu_dong_trong() {
            ImportPreviewResponse res = service.preview(USER_ID,
                    new ImportPreviewRequest(1, null, Arrays.asList((ImportRowInput) null)));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.ERROR);
            assertThat(r.messages()).contains("Thiếu cả chữ Hán lẫn pinyin");
        }

        @Test
        @DisplayName("Không nhập pinyin thì lấy từ mục từ điển đầu tiên, pinyinMatchesDictionary = null")
        void khong_co_pinyin_thi_lay_tu_dien() {
            when(cedictService.lookupSimplified("学习")).thenReturn(List.of(XUEXI));

            ImportPreviewResponse res = service.preview(USER_ID, request(row("学习", null, "học")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.OK);
            assertThat(r.pinyinMatchesDictionary()).isNull();
            assertThat(r.pinyin()).isEqualTo("xuéxí");
            assertThat(r.pinyinNumbered()).isEqualTo("xue2 xi2");
        }

        @Test
        @DisplayName("Chữ Hán không có trong CC-CEDICT thì WARNING nhưng vẫn cho tạo")
        void khong_co_trong_tu_dien_thi_warning_van_tao() {
            ImportPreviewResponse res = service.preview(USER_ID, request(row("做什么", "zuò shénme", "làm gì")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.WARNING);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.CREATE);
            assertThat(r.dictionaryFound()).isFalse();
            assertThat(r.pinyinMatchesDictionary()).isNull();
            assertThat(r.pinyin()).isEqualTo("zuò shénme");
            assertThat(r.messages()).anyMatch(m -> m.contains("CC-CEDICT"));
        }

        @Test
        @DisplayName("Thiếu nghĩa tiếng Việt thì ERROR")
        void thieu_nghia_viet_thi_error() {
            when(cedictService.lookupSimplified("学习")).thenReturn(List.of(XUEXI));

            ImportPreviewResponse res = service.preview(USER_ID, request(row("学习", "xuéxí", "")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.ERROR);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.SKIP);
            assertThat(r.messages()).contains("Thiếu nghĩa tiếng Việt");
        }
    }

    @Nested
    @DisplayName("Preview — trùng lặp")
    class TrungLap {

        @Test
        @DisplayName("Trùng hệ thống (cùng chữ + cùng pinyin) thì EXISTS + LINK kèm cờ đã học")
        void trung_he_thong_thi_exists_link() {
            when(cedictService.lookupSimplified("你好")).thenReturn(List.of(NIHAO));
            when(lookupRepository.findBySimplified("你好"))
                    .thenReturn(List.of(word(5L, "你好", "nǐ hǎo", "xin chào")));
            when(userWordService.learnedWordIds(USER_ID)).thenReturn(List.of(5L));

            ImportPreviewResponse res = service.preview(USER_ID, request(row("你好", "nǐ hǎo", "chào bạn")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.EXISTS);
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.LINK);
            assertThat(r.existingWord()).isNotNull();
            assertThat(r.existingWord().id()).isEqualTo(5L);
            assertThat(r.existingWord().meaningVi()).isEqualTo("xin chào");
            assertThat(r.existingWord().alreadyLearned()).isTrue();
            assertThat(r.messages()).contains("Đã có trong hệ thống (id 5)");
            assertThat(res.summary().existing()).isEqualTo(1);
            assertThat(res.summary().willCreate()).isZero();
        }

        @Test
        @DisplayName("Trùng hệ thống nhưng chưa học thì alreadyLearned = false; thiếu nghĩa Việt vẫn không ERROR")
        void trung_he_thong_chua_hoc() {
            when(cedictService.lookupSimplified("你好")).thenReturn(List.of(NIHAO));
            when(lookupRepository.findBySimplified("你好"))
                    .thenReturn(List.of(word(5L, "你好", "nǐ hǎo", "xin chào")));

            ImportPreviewResponse res = service.preview(USER_ID, request(row("你好", "ni3 hao3", null)));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.status()).isEqualTo(ImportRowStatus.EXISTS);
            assertThat(r.existingWord().alreadyLearned()).isFalse();
            assertThat(r.messages()).doesNotContain("Thiếu nghĩa tiếng Việt");
        }

        @Test
        @DisplayName("Cùng chữ Hán nhưng pinyin khác (từ đa âm) thì chỉ thêm ghi chú, vẫn CREATE")
        void cung_chu_khac_pinyin_thi_khong_trung() {
            when(lookupRepository.findBySimplified("好"))
                    .thenReturn(List.of(word(7L, "好", "hǎo", "tốt")));

            ImportPreviewResponse res = service.preview(USER_ID, request(row("好", "hào", "thích")));

            ImportPreviewRow r = res.rows().get(0);
            assertThat(r.existingWord()).isNull();
            assertThat(r.suggestedAction()).isEqualTo(ImportAction.CREATE);
            assertThat(r.messages()).anyMatch(m -> m.contains("Hệ thống đã có 好 với pinyin hǎo"));
        }

        @Test
        @DisplayName("Trùng ngay trong lô thì dòng sau ERROR, chỉ dòng đầu được tạo")
        void trung_trong_lo_thi_error() {
            when(cedictService.lookupSimplified("学习")).thenReturn(List.of(XUEXI));

            ImportPreviewResponse res = service.preview(USER_ID, request(
                    row("学习", "xuéxí", "học"),
                    row("学习", "xue2 xi2", "học tập")));

            ImportPreviewRow first = res.rows().get(0);
            ImportPreviewRow second = res.rows().get(1);
            assertThat(first.index()).isZero();
            assertThat(first.status()).isEqualTo(ImportRowStatus.OK);
            assertThat(first.suggestedAction()).isEqualTo(ImportAction.CREATE);
            assertThat(second.index()).isEqualTo(1);
            assertThat(second.status()).isEqualTo(ImportRowStatus.ERROR);
            assertThat(second.suggestedAction()).isEqualTo(ImportAction.SKIP);
            assertThat(second.messages()).contains("Trùng với dòng 1");
            assertThat(res.summary().total()).isEqualTo(2);
            assertThat(res.summary().willCreate()).isEqualTo(1);
            assertThat(res.summary().errors()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Confirm")
    class XacNhan {

        private ImportConfirmRow confirmRow(ImportAction action, Long existingWordId, String simplified) {
            return new ImportConfirmRow(action, existingWordId, simplified, null, "pīnyīn", null,
                    "nghĩa", null, null, 1, null);
        }

        @Test
        @DisplayName("Đếm đúng từng loại, dòng lỗi không làm hỏng dòng khác, đánh dấu đã học các id thành công")
        void confirm_dem_dung_va_danh_dau_da_hoc() {
            ImportConfirmRow create = confirmRow(ImportAction.CREATE, null, "学习");
            ImportConfirmRow link = confirmRow(ImportAction.LINK, 5L, null);
            ImportConfirmRow skip = confirmRow(ImportAction.SKIP, null, "好");
            ImportConfirmRow failing = confirmRow(ImportAction.CREATE, null, "你好");
            when(rowExecutor.execute(create)).thenReturn(10L);
            when(rowExecutor.execute(link)).thenReturn(5L);
            when(rowExecutor.execute(skip)).thenReturn(null);
            when(rowExecutor.execute(failing)).thenThrow(new ConflictException("Đã tồn tại"));
            when(userWordService.bulkUpsert(eq(USER_ID), any(), eq(UserWordStatus.LEARNED))).thenReturn(2);

            ImportConfirmResponse res = service.confirm(USER_ID,
                    new ImportConfirmRequest(true, List.of(create, link, skip, failing)));

            assertThat(res.created()).isEqualTo(1);
            assertThat(res.linked()).isEqualTo(1);
            assertThat(res.updated()).isZero();
            assertThat(res.skipped()).isEqualTo(1);
            assertThat(res.wordIds()).containsExactly(10L, 5L);
            assertThat(res.errors()).hasSize(1);
            assertThat(res.errors().get(0).index()).isEqualTo(3);
            assertThat(res.errors().get(0).message()).isEqualTo("Đã tồn tại");
            assertThat(res.markedLearned()).isEqualTo(2);
            verify(userWordService).bulkUpsert(USER_ID, List.of(10L, 5L), UserWordStatus.LEARNED);
        }

        @Test
        @DisplayName("Lỗi ràng buộc DB và lỗi bất ngờ của một dòng đều được ghi vào errors, dòng khác vẫn chạy")
        void loi_db_va_loi_bat_ngo_khong_dung_vong_lap() {
            ImportConfirmRow dup = confirmRow(ImportAction.CREATE, null, "你好");
            ImportConfirmRow boom = confirmRow(ImportAction.LINK, 5L, null);
            ImportConfirmRow ok = confirmRow(ImportAction.CREATE, null, "学习");
            when(rowExecutor.execute(dup)).thenThrow(new DataIntegrityViolationException("uk_words_simplified_pinyin"));
            when(rowExecutor.execute(boom)).thenThrow(new IllegalStateException("bùm"));
            when(rowExecutor.execute(ok)).thenReturn(10L);

            ImportConfirmResponse res = service.confirm(USER_ID,
                    new ImportConfirmRequest(false, Arrays.asList(dup, boom, null, ok)));

            assertThat(res.created()).isEqualTo(1);
            assertThat(res.wordIds()).containsExactly(10L);
            assertThat(res.errors()).extracting(e -> e.index()).containsExactly(0, 1, 2);
            assertThat(res.errors().get(0).message()).contains("ràng buộc");
            assertThat(res.errors().get(1).message()).contains("bùm");
            assertThat(res.errors().get(2).message()).isEqualTo("Dòng trống");
        }

        @Test
        @DisplayName("bulkUpsert thất bại thì vẫn giữ kết quả đã ghi, báo lỗi index -1, markedLearned = 0")
        void bulk_upsert_that_bai_khong_lam_mat_ket_qua() {
            ImportConfirmRow create = confirmRow(ImportAction.CREATE, null, "学习");
            when(rowExecutor.execute(create)).thenReturn(10L);
            when(userWordService.bulkUpsert(eq(USER_ID), any(), eq(UserWordStatus.LEARNED)))
                    .thenThrow(new IllegalStateException("DB tạm thời không ghi được"));

            ImportConfirmResponse res = service.confirm(USER_ID,
                    new ImportConfirmRequest(true, List.of(create)));

            assertThat(res.created()).isEqualTo(1);
            assertThat(res.wordIds()).containsExactly(10L);
            assertThat(res.markedLearned()).isZero();
            assertThat(res.errors()).hasSize(1);
            assertThat(res.errors().get(0).index()).isEqualTo(-1);
        }

        @Test
        @DisplayName("markAsLearned = false thì không gọi bulkUpsert")
        void khong_danh_dau_khi_tat_co() {
            ImportConfirmRow create = confirmRow(ImportAction.CREATE, null, "学习");
            when(rowExecutor.execute(create)).thenReturn(10L);

            ImportConfirmResponse res = service.confirm(USER_ID,
                    new ImportConfirmRequest(false, List.of(create)));

            assertThat(res.created()).isEqualTo(1);
            assertThat(res.markedLearned()).isZero();
            verify(userWordService, never()).bulkUpsert(any(), any(), any());
        }
    }
}
