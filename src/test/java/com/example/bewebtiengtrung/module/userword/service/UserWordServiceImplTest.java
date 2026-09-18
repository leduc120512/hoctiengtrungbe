package com.example.bewebtiengtrung.module.userword.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.user.repository.UserRepository;
import com.example.bewebtiengtrung.module.userword.dto.UpsertUserWordRequest;
import com.example.bewebtiengtrung.module.userword.dto.UserWordResponse;
import com.example.bewebtiengtrung.module.userword.dto.UserWordStatsResponse;
import com.example.bewebtiengtrung.module.userword.entity.UserWord;
import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import com.example.bewebtiengtrung.module.userword.mapper.UserWordMapper;
import com.example.bewebtiengtrung.module.userword.repository.UserWordRepository;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.module.vocabulary.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Kiểm thử nghiệp vụ sổ từ đã học với repository giả lập (không cần MySQL).
 */
@ExtendWith(MockitoExtension.class)
class UserWordServiceImplTest {

    private static final Long USER_ID = 1L;

    @Mock
    private UserWordRepository userWordRepository;
    @Mock
    private WordRepository wordRepository;
    @Mock
    private UserRepository userRepository;

    private UserWordServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserWordServiceImpl(userWordRepository, wordRepository, userRepository, new UserWordMapper());
    }

    private static Word word(long id, String simplified, int hskLevel) {
        Word w = Word.builder()
                .simplified(simplified)
                .traditional(simplified)
                .pinyin("pinyin")
                .pinyinNumbered("pin1 yin1")
                .meaningVi("nghĩa")
                .hskLevel(hskLevel)
                .build();
        w.setId(id);
        return w;
    }

    @Test
    @DisplayName("list: không có sort thì áp mặc định learnedAt DESC và bỏ qua từ khoá trống")
    void list_ap_sort_mac_dinh() {
        UserWord uw = UserWord.builder().word(word(10L, "学习", 1)).status(UserWordStatus.LEARNED)
                .learnedAt(Instant.now()).build();
        uw.setId(5L);
        Page<UserWord> page = new PageImpl<>(List.of(uw));
        when(userWordRepository.search(eq(USER_ID), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<UserWordResponse> result = service.list(USER_ID, null, null, "   ", PageRequest.of(0, 20));

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).simplified()).isEqualTo("学习");
        assertThat(result.content().get(0).wordId()).isEqualTo(10L);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userWordRepository).search(eq(USER_ID), isNull(), isNull(), isNull(), captor.capture());
        Sort sort = captor.getValue().getSort();
        assertThat(sort.getOrderFor("learnedAt")).isNotNull();
        assertThat(sort.getOrderFor("learnedAt").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("list: Pageable.unpaged() được giữ nguyên là unpaged (module sinh câu dùng)")
    void list_giu_unpaged() {
        when(userWordRepository.search(eq(USER_ID), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.list(USER_ID, null, null, null, Pageable.unpaged());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userWordRepository).search(eq(USER_ID), isNull(), isNull(), isNull(), captor.capture());
        assertThat(captor.getValue().isUnpaged()).isTrue();
    }

    @Test
    @DisplayName("upsert: wordId không tồn tại → NotFoundException")
    void upsert_khong_tim_thay_tu() {
        when(wordRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.upsert(USER_ID, 99L, new UpsertUserWordRequest(UserWordStatus.LEARNED, null)))
                .isInstanceOf(NotFoundException.class);
        verify(userWordRepository, never()).save(any());
    }

    @Test
    @DisplayName("upsert: từ chưa có trong sổ → tạo mới với learnedAt, ghi chú trống lưu NULL")
    void upsert_tao_moi() {
        Word w = word(10L, "你好", 1);
        when(wordRepository.findById(10L)).thenReturn(Optional.of(w));
        when(userWordRepository.findByUserIdAndWordId(USER_ID, 10L)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(USER_ID)).thenReturn(new User());
        when(userWordRepository.save(any(UserWord.class))).thenAnswer(inv -> inv.getArgument(0));

        UserWordResponse response = service.upsert(USER_ID, 10L,
                new UpsertUserWordRequest(UserWordStatus.LEARNING, "   "));

        assertThat(response.status()).isEqualTo(UserWordStatus.LEARNING);
        assertThat(response.note()).isNull();
        assertThat(response.learnedAt()).isNotNull();
        assertThat(response.simplified()).isEqualTo("你好");
    }

    @Test
    @DisplayName("upsert: từ đã có → cập nhật status/note, giữ nguyên learnedAt")
    void upsert_cap_nhat() {
        Word w = word(10L, "你好", 1);
        Instant original = Instant.parse("2026-01-01T00:00:00Z");
        UserWord existing = UserWord.builder().word(w).status(UserWordStatus.LEARNED)
                .learnedAt(original).note("cũ").build();
        existing.setId(7L);
        when(wordRepository.findById(10L)).thenReturn(Optional.of(w));
        when(userWordRepository.findByUserIdAndWordId(USER_ID, 10L)).thenReturn(Optional.of(existing));
        when(userWordRepository.save(any(UserWord.class))).thenAnswer(inv -> inv.getArgument(0));

        UserWordResponse response = service.upsert(USER_ID, 10L,
                new UpsertUserWordRequest(UserWordStatus.MASTERED, " mới "));

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.status()).isEqualTo(UserWordStatus.MASTERED);
        assertThat(response.note()).isEqualTo("mới");
        assertThat(response.learnedAt()).isEqualTo(original);
        verify(userRepository, never()).getReferenceById(anyLong());
    }

    @Test
    @DisplayName("bulkUpsert: bỏ trùng, bỏ id đã có trong sổ, bỏ id không tồn tại; trả số dòng tạo mới")
    void bulkUpsert_idempotent() {
        when(userWordRepository.findExistingWordIds(eq(USER_ID), anyCollection())).thenReturn(List.of(2L));
        // id 4 không tồn tại trong bảng words nên repository không trả về
        when(wordRepository.findAllById(any())).thenReturn(List.of(word(1L, "一", 1), word(2L, "二", 1), word(3L, "三", 2)));
        when(userRepository.getReferenceById(USER_ID)).thenReturn(new User());
        when(userWordRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // Arrays.asList vì List.of không cho phần tử null
        int created = service.bulkUpsert(USER_ID, Arrays.asList(1L, 2L, 3L, 3L, null, 4L), UserWordStatus.LEARNED);

        assertThat(created).isEqualTo(2);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserWord>> captor = ArgumentCaptor.forClass(List.class);
        verify(userWordRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).extracting(uw -> uw.getWord().getId()).containsExactly(1L, 3L);
        assertThat(captor.getValue()).allSatisfy(uw -> {
            assertThat(uw.getStatus()).isEqualTo(UserWordStatus.LEARNED);
            assertThat(uw.getLearnedAt()).isNotNull();
        });
    }

    @Test
    @DisplayName("bulkUpsert: danh sách rỗng hoặc toàn null → 0 và không chạm DB")
    void bulkUpsert_rong() {
        assertThat(service.bulkUpsert(USER_ID, List.of(), UserWordStatus.LEARNED)).isZero();
        assertThat(service.bulkUpsert(USER_ID, Arrays.asList((Long) null), UserWordStatus.LEARNED)).isZero();
        verify(userWordRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("remove: từ chưa có trong sổ → NotFoundException")
    void remove_khong_co() {
        when(userWordRepository.findByUserIdAndWordId(USER_ID, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remove(USER_ID, 10L)).isInstanceOf(NotFoundException.class);
        verify(userWordRepository, never()).delete(any());
    }

    @Test
    @DisplayName("stats: luôn đủ 3 trạng thái, cấp HSK tăng dần, learnedThisWeek dùng mốc 7 ngày")
    void stats_day_du() {
        when(userWordRepository.countByUserId(USER_ID)).thenReturn(12L);
        when(userWordRepository.countGroupByStatus(USER_ID)).thenReturn(List.of(
                statusCount(UserWordStatus.LEARNED, 10L),
                statusCount(UserWordStatus.MASTERED, 2L)));
        when(userWordRepository.countGroupByHskLevel(USER_ID)).thenReturn(List.of(
                levelCount(2, 4L), levelCount(1, 8L)));
        when(userWordRepository.countByUserIdAndLearnedAtGreaterThanEqual(eq(USER_ID), any(Instant.class)))
                .thenReturn(3L);

        UserWordStatsResponse stats = service.stats(USER_ID);

        assertThat(stats.total()).isEqualTo(12L);
        assertThat(stats.byStatus()).containsEntry(UserWordStatus.LEARNING, 0L)
                .containsEntry(UserWordStatus.LEARNED, 10L)
                .containsEntry(UserWordStatus.MASTERED, 2L);
        assertThat(stats.byHskLevel().keySet()).containsExactly(1, 2);
        assertThat(stats.byHskLevel()).containsEntry(1, 8L).containsEntry(2, 4L);
        assertThat(stats.learnedThisWeek()).isEqualTo(3L);

        ArgumentCaptor<Instant> since = ArgumentCaptor.forClass(Instant.class);
        verify(userWordRepository).countByUserIdAndLearnedAtGreaterThanEqual(eq(USER_ID), since.capture());
        Instant expected = Instant.now().minusSeconds(7L * 24 * 3600);
        assertThat(since.getValue()).isBetween(expected.minusSeconds(5), expected.plusSeconds(5));
    }

    private static UserWordRepository.StatusCount statusCount(UserWordStatus status, long total) {
        return new UserWordRepository.StatusCount() {
            @Override
            public UserWordStatus getStatus() {
                return status;
            }

            @Override
            public long getTotal() {
                return total;
            }
        };
    }

    private static UserWordRepository.LevelCount levelCount(int level, long total) {
        return new UserWordRepository.LevelCount() {
            @Override
            public Integer getHskLevel() {
                return level;
            }

            @Override
            public long getTotal() {
                return total;
            }
        };
    }
}
