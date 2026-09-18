package com.example.bewebtiengtrung.module.sentence.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.common.exception.BadRequestException;
import com.example.bewebtiengtrung.common.exception.ForbiddenException;
import com.example.bewebtiengtrung.module.ai.dto.GeneratedSentence;
import com.example.bewebtiengtrung.module.ai.dto.LearnedWordBrief;
import com.example.bewebtiengtrung.module.ai.service.AiSentenceGenerator;
import com.example.bewebtiengtrung.module.sentence.dto.BulkSentenceRequest;
import com.example.bewebtiengtrung.module.sentence.dto.BulkSentenceResponse;
import com.example.bewebtiengtrung.module.sentence.dto.GenerateSentencesRequest;
import com.example.bewebtiengtrung.module.sentence.dto.GenerateSentencesResponse;
import com.example.bewebtiengtrung.module.sentence.dto.SentenceInput;
import com.example.bewebtiengtrung.module.sentence.dto.SentenceResponse;
import com.example.bewebtiengtrung.module.sentence.entity.SentenceSource;
import com.example.bewebtiengtrung.module.sentence.entity.UserSentence;
import com.example.bewebtiengtrung.module.sentence.mapper.SentenceMapper;
import com.example.bewebtiengtrung.module.sentence.repository.UserSentenceRepository;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.userword.dto.UserWordResponse;
import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import com.example.bewebtiengtrung.module.userword.service.UserWordService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Kiểm thử pipeline sinh câu bằng AI của {@link SentenceServiceImpl} với {@link AiSentenceGenerator} giả.
 *
 * <p>Vốn từ mẫu: 我 你 他 是 学生 老师 好 吗 不 — mọi câu chỉ dùng các chữ này mới hợp lệ.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SentenceServiceImplTest {

    private static final Long USER_ID = 1L;

    private static final List<String> LEARNED = List.of("我", "你", "他", "是", "学生", "老师", "好", "吗", "不");

    @Mock
    private UserSentenceRepository sentenceRepository;

    @Mock
    private UserWordService userWordService;

    @Mock
    private AiSentenceGenerator aiGenerator;

    @Mock
    private EntityManager entityManager;

    private SentenceServiceImpl service;

    /** Bộ đếm id giả cho các entity được "lưu". */
    private final AtomicLong idSequence = new AtomicLong(100);

    @BeforeEach
    void setUp() {
        // TransactionTemplate thật trên PlatformTransactionManager giả: callback vẫn chạy, không cần DB.
        TransactionTemplate transactionTemplate = new TransactionTemplate(mock(PlatformTransactionManager.class));
        service = new SentenceServiceImpl(sentenceRepository, new SentenceMapper(), userWordService,
                aiGenerator, entityManager, transactionTemplate);

        when(aiGenerator.isEnabled()).thenReturn(true);
        when(aiGenerator.model()).thenReturn("claude-opus-5");

        User user = new User();
        user.setId(USER_ID);
        when(entityManager.getReference(User.class, USER_ID)).thenReturn(user);

        when(userWordService.list(eq(USER_ID), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(pageOf(LEARNED));

        // Câu đã có sẵn trong kho: "我是学生。"
        when(sentenceRepository.findHanziKeysByUserId(USER_ID)).thenReturn(List.of("我是学生"));
        when(sentenceRepository.findHanziByUserId(USER_ID)).thenReturn(List.of("我是学生。"));

        // saveAll: gán id tăng dần rồi trả lại đúng danh sách
        when(sentenceRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<UserSentence> entities = invocation.getArgument(0);
            List<UserSentence> saved = new ArrayList<>(entities.size());
            for (UserSentence entity : entities) {
                entity.setId(idSequence.incrementAndGet());
                saved.add(entity);
            }
            return saved;
        });
    }

    // ------------------------------------------------------------------
    // Trợ giúp dựng dữ liệu
    // ------------------------------------------------------------------

    private static PageResponse<UserWordResponse> pageOf(List<String> words) {
        List<UserWordResponse> content = new ArrayList<>();
        long id = 1;
        for (String w : words) {
            content.add(new UserWordResponse(id, id, w, w, "pinyin", "nghĩa", null, 1, null,
                    UserWordStatus.LEARNED, null, Instant.now()));
            id++;
        }
        return new PageResponse<>(content, 0, content.size(), content.size(), 1, true, true);
    }

    private static GeneratedSentence gs(String hanzi, int level) {
        return new GeneratedSentence(hanzi, "Pīnyīn.", "Nghĩa.", level);
    }

    private static GenerateSentencesRequest request(int count, Integer level) {
        return new GenerateSentencesRequest(count, level, null);
    }

    // ------------------------------------------------------------------
    // generate
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("generate — pipeline lọc câu AI")
    class Generate {

        @Test
        @DisplayName("Câu có chữ lạ bị loại, đếm vào rejected và có mẫu 'câu (lạ: chữ)'")
        void chu_la_bi_loai_va_dem() {
            when(aiGenerator.generate(anyList(), anyList(), anyInt(), any(), any())).thenReturn(List.of(
                    gs("你好吗？", 1),
                    gs("他很高兴。", 1),        // 很 高 兴 chưa học
                    gs("我是老师。", 1),
                    gs("你是学生吗？", 2)));

            GenerateSentencesResponse res = service.generate(USER_ID, request(3, null));

            assertThat(res.requested()).isEqualTo(3);
            assertThat(res.generated()).isEqualTo(3);
            assertThat(res.rejected()).isEqualTo(1);
            assertThat(res.duplicates()).isZero();
            assertThat(res.rejectedSamples()).containsExactly("他很高兴。 (lạ: 很高兴)");
            assertThat(res.model()).isEqualTo("claude-opus-5");
            assertThat(res.sentences()).extracting(SentenceResponse::hanzi)
                    .containsExactly("你好吗？", "我是老师。", "你是学生吗？");
            assertThat(res.sentences()).extracting(SentenceResponse::source)
                    .containsOnly(SentenceSource.AI);

            // Đủ 100% ngay lần đầu ⇒ không gọi lại
            verify(aiGenerator, times(1)).generate(anyList(), anyList(), anyInt(), any(), any());
        }

        @Test
        @DisplayName("Câu trùng với câu đã có hoặc trùng trong cùng đợt bị loại theo hanzi_key")
        void cau_trung_bi_loai() {
            when(aiGenerator.generate(anyList(), anyList(), anyInt(), any(), any())).thenReturn(List.of(
                    gs("我 是 学生！", 1),       // trùng câu đã có "我是学生。" (khác dấu câu / khoảng trắng)
                    gs("你好吗？", 1),
                    gs("你好吗?", 1),           // trùng với ứng viên ngay trước trong cùng đợt
                    gs("他是老师。", 1)));

            GenerateSentencesResponse res = service.generate(USER_ID, request(2, null));

            assertThat(res.generated()).isEqualTo(2);
            assertThat(res.duplicates()).isEqualTo(2);
            assertThat(res.rejected()).isZero();
            assertThat(res.sentences()).extracting(SentenceResponse::hanzi)
                    .containsExactly("你好吗？", "他是老师。");

            // Entity lưu xuống có hanzi_key chuẩn hoá
            ArgumentCaptor<List<UserSentence>> captor = ArgumentCaptor.captor();
            verify(sentenceRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).extracting(UserSentence::getHanziKey)
                    .containsExactly("你好吗", "他是老师");
        }

        @Test
        @DisplayName("Câu chỉ là câu đã có (hoặc câu trong đợt) đổi chỗ chữ bị loại, đếm vào reordered")
        void cau_doi_cho_bi_loai() {
            when(aiGenerator.generate(anyList(), anyList(), anyInt(), any(), any())).thenReturn(List.of(
                    gs("学生是我。", 1),         // 我是学生 đổi chỗ — câu đã có
                    gs("你是老师吗？", 2),
                    gs("老师是你吗？", 2),       // đổi chỗ câu ngay trên trong cùng đợt
                    gs("他不是学生。", 1)));

            GenerateSentencesResponse res = service.generate(USER_ID, request(2, null));

            assertThat(res.generated()).isEqualTo(2);
            assertThat(res.reordered()).isEqualTo(2);
            assertThat(res.duplicates()).isZero();
            assertThat(res.rejected()).isZero();
            assertThat(res.rejectedSamples()).containsExactly(
                    "学生是我。 (đổi chỗ câu đã có)", "老师是你吗？ (đổi chỗ câu đã có)");
            assertThat(res.sentences()).extracting(SentenceResponse::hanzi)
                    .containsExactly("你是老师吗？", "他不是学生。");
        }

        @Test
        @DisplayName("Từ ưu tiên (focusWords) được chuyển nguyên vẹn cho AI")
        void focus_words_duoc_chuyen_cho_ai() {
            when(aiGenerator.generate(anyList(), anyList(), anyInt(), any(), any()))
                    .thenReturn(List.of(gs("他是老师。", 1)));

            service.generate(USER_ID, new GenerateSentencesRequest(1, null, List.of("老师", "学生")));

            verify(aiGenerator).generate(anyList(), anyList(), eq(1), isNull(), eq(List.of("老师", "学生")));
        }

        @Test
        @DisplayName("Sau lần 1 còn dưới 60% thì gọi AI thêm đúng một lần, xin phần thiếu và tránh câu vừa tạo")
        void goi_lai_lan_hai_khi_thieu() {
            when(aiGenerator.generate(anyList(), anyList(), anyInt(), any(), any()))
                    // Lần 1: chỉ 2/10 hợp lệ (20% < 60%)
                    .thenReturn(List.of(
                            gs("你好吗？", 1),
                            gs("他很高兴。", 1),
                            gs("我是老师。", 1)))
                    // Lần 2: thêm 5 hợp lệ + 1 trùng lần 1
                    .thenReturn(List.of(
                            gs("他是学生。", 1),
                            gs("你好吗？", 1),
                            gs("你不是老师。", 2),
                            gs("我不好。", 1),
                            gs("他好吗？", 1),
                            gs("你是我老师。", 2)));

            GenerateSentencesResponse res = service.generate(USER_ID, request(10, null));

            assertThat(res.generated()).isEqualTo(7);
            assertThat(res.rejected()).isEqualTo(1);
            assertThat(res.duplicates()).isEqualTo(1);

            ArgumentCaptor<List<String>> avoidCaptor = ArgumentCaptor.captor();
            ArgumentCaptor<Integer> countCaptor = ArgumentCaptor.captor();
            verify(aiGenerator, times(2)).generate(anyList(), avoidCaptor.capture(), countCaptor.capture(),
                    any(), any());

            // Lần 1 xin đủ 10, lần 2 chỉ xin phần thiếu 8
            assertThat(countCaptor.getAllValues()).containsExactly(10, 8);
            // Lần 2 phải kèm cả câu vừa tạo ở lần 1 vào danh sách "đừng tạo lại"
            assertThat(avoidCaptor.getAllValues().get(1)).contains("我是学生。", "你好吗？", "我是老师。");
        }

        @Test
        @DisplayName("Tối đa 2 lần gọi kể cả khi lần 2 vẫn còn thiếu")
        void toi_da_hai_lan_goi() {
            when(aiGenerator.generate(anyList(), anyList(), anyInt(), any(), any()))
                    .thenReturn(List.of(gs("他很高兴。", 1)))   // toàn chữ lạ
                    .thenReturn(List.of(gs("你好吗？", 1)));

            GenerateSentencesResponse res = service.generate(USER_ID, request(10, null));

            assertThat(res.generated()).isEqualTo(1);
            assertThat(res.rejected()).isEqualTo(1);
            verify(aiGenerator, times(2)).generate(anyList(), anyList(), anyInt(), any(), any());
        }

        @Test
        @DisplayName("Đã đủ ≥ 60% sau lần 1 thì không gọi lại")
        void du_60_phan_tram_khong_goi_lai() {
            when(aiGenerator.generate(anyList(), anyList(), anyInt(), any(), any()))
                    .thenReturn(List.of(gs("你好吗？", 1), gs("他是老师。", 1), gs("我不好。", 1)));

            GenerateSentencesResponse res = service.generate(USER_ID, request(5, null));

            assertThat(res.generated()).isEqualTo(3);   // 3/5 = 60%
            verify(aiGenerator, times(1)).generate(anyList(), anyList(), anyInt(), any(), any());
        }

        @Test
        @DisplayName("AI chưa cấu hình ⇒ ApiException 503 AI_DISABLED, không gọi gì khác")
        void ai_tat_503() {
            when(aiGenerator.isEnabled()).thenReturn(false);

            assertThatThrownBy(() -> service.generate(USER_ID, request(5, null)))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        ApiException api = (ApiException) ex;
                        assertThat(api.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                        assertThat(api.getCode()).isEqualTo("AI_DISABLED");
                        assertThat(api.getMessage()).isEqualTo("Chưa cấu hình ANTHROPIC_API_KEY trên máy chủ");
                    });

            verify(userWordService, never()).list(any(), any(), any(), any(), any());
            verify(aiGenerator, never()).generate(anyList(), anyList(), anyInt(), any(), any());
        }

        @Test
        @DisplayName("Dưới 5 từ đã học ⇒ BadRequest, không gọi AI")
        void duoi_5_tu_bad_request() {
            when(userWordService.list(eq(USER_ID), isNull(), isNull(), isNull(), any(Pageable.class)))
                    .thenReturn(pageOf(List.of("我", "你", "他", "是")));

            assertThatThrownBy(() -> service.generate(USER_ID, request(5, null)))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("ít nhất 5 từ");

            verify(aiGenerator, never()).generate(anyList(), anyList(), anyInt(), any(), any());
        }

        @Test
        @DisplayName("Gọi UserWordService đúng chữ ký: (userId, null, null, null, unpaged) và truyền từ cho AI")
        void goi_user_word_service_dung_chu_ky() {
            when(aiGenerator.generate(anyList(), anyList(), anyInt(), any(), any()))
                    .thenReturn(List.of(gs("你好吗？", 1)));

            service.generate(USER_ID, new GenerateSentencesRequest(1, 2, List.of("你")));

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.captor();
            verify(userWordService).list(eq(USER_ID), isNull(), isNull(), isNull(), pageableCaptor.capture());
            assertThat(pageableCaptor.getValue().isUnpaged()).isTrue();

            ArgumentCaptor<List<LearnedWordBrief>> wordsCaptor = ArgumentCaptor.captor();
            verify(aiGenerator).generate(wordsCaptor.capture(), anyList(), eq(1), eq(2), eq(List.of("你")));
            assertThat(wordsCaptor.getValue()).extracting(LearnedWordBrief::hanzi)
                    .containsExactlyElementsOf(LEARNED);
        }

        @Test
        @DisplayName("Người dùng chọn cấp thì mọi câu lưu với cấp đó; không chọn thì lấy cấp AI trả về")
        void cap_do_luu() {
            when(aiGenerator.generate(anyList(), anyList(), anyInt(), any(), any()))
                    .thenReturn(List.of(gs("你好吗？", 3)));

            GenerateSentencesResponse fixed = service.generate(USER_ID, request(1, 2));
            assertThat(fixed.sentences().get(0).level()).isEqualTo(2);

            // Reset kho: câu vừa lưu không nằm trong DB giả nên vẫn tạo lại được
            GenerateSentencesResponse free = service.generate(USER_ID, request(1, null));
            assertThat(free.sentences().get(0).level()).isEqualTo(3);
        }
    }

    // ------------------------------------------------------------------
    // addBulk / delete / deleteBySource
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("addBulk — thêm nhiều câu")
    class AddBulk {

        @Test
        @DisplayName("Bỏ câu trùng với câu đã có và trùng trong cùng đợt, đếm duplicates")
        void bo_trung() {
            BulkSentenceRequest req = new BulkSentenceRequest(List.of(
                    new SentenceInput("我是学生。", "Wǒ shì xuésheng.", "Tôi là học sinh.", 1),  // đã có
                    new SentenceInput("你好吗？", "Nǐ hǎo ma?", "Bạn khoẻ không?", null),
                    new SentenceInput("你 好 吗?", "Nǐ hǎo ma?", "Bạn khoẻ không?", 1),          // trùng trong đợt
                    new SentenceInput("他是老师。", "Tā shì lǎoshī.", "Anh ấy là giáo viên.", 2)));

            BulkSentenceResponse res = service.addBulk(USER_ID, req, SentenceSource.MANUAL);

            assertThat(res.added()).isEqualTo(2);
            assertThat(res.duplicates()).isEqualTo(2);
            assertThat(res.sentences()).extracting(SentenceResponse::hanzi).containsExactly("你好吗？", "他是老师。");
            assertThat(res.sentences()).extracting(SentenceResponse::level).containsExactly(1, 2);
            assertThat(res.sentences()).extracting(SentenceResponse::source).containsOnly(SentenceSource.MANUAL);
        }

        @Test
        @DisplayName("Câu không có chữ Hán ⇒ BadRequest")
        void khong_co_chu_han() {
            BulkSentenceRequest req = new BulkSentenceRequest(List.of(
                    new SentenceInput("hello", "hello", "xin chào", 1)));

            assertThatThrownBy(() -> service.addBulk(USER_ID, req, SentenceSource.MANUAL))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @Nested
    @DisplayName("delete / deleteBySource")
    class Delete {

        @Test
        @DisplayName("Xoá câu của người khác ⇒ Forbidden")
        void xoa_cau_nguoi_khac() {
            UserSentence other = UserSentence.builder().id(7L).userId(99L).hanzi("你好").hanziKey("你好").build();
            when(sentenceRepository.findById(7L)).thenReturn(Optional.of(other));

            assertThatThrownBy(() -> service.delete(USER_ID, 7L)).isInstanceOf(ForbiddenException.class);
            verify(sentenceRepository, never()).delete(any(UserSentence.class));
        }

        @Test
        @DisplayName("Không cho xoá hàng loạt câu BUILTIN; AI/MANUAL thì trả số dòng đã xoá")
        void khong_xoa_builtin() {
            assertThatThrownBy(() -> service.deleteBySource(USER_ID, SentenceSource.BUILTIN))
                    .isInstanceOf(BadRequestException.class);

            when(sentenceRepository.deleteByUserIdAndSource(USER_ID, SentenceSource.AI)).thenReturn(4);
            assertThat(service.deleteBySource(USER_ID, SentenceSource.AI)).isEqualTo(4);
        }
    }
}
