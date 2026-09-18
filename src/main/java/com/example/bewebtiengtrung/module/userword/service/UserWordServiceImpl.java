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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Cài đặt nghiệp vụ sổ từ đã học. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWordServiceImpl implements UserWordService {

    /** Thứ tự mặc định khi client không truyền sort: từ mới thêm lên trước. */
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "learnedAt")
            .and(Sort.by(Sort.Direction.DESC, "id"));

    /** Cửa sổ thống kê "học trong tuần này". */
    private static final long WEEK_DAYS = 7L;

    private final UserWordRepository userWordRepository;
    private final WordRepository wordRepository;
    private final UserRepository userRepository;
    private final UserWordMapper userWordMapper;

    @Override
    public PageResponse<UserWordResponse> list(Long userId, UserWordStatus status, Integer hskLevel,
                                               String q, Pageable pageable) {
        Page<UserWord> page = userWordRepository.search(
                userId, status, hskLevel, normalizeQuery(q), withDefaultSort(pageable));
        return PageResponse.of(page, userWordMapper::toResponse);
    }

    @Override
    @Transactional
    public UserWordResponse upsert(Long userId, Long wordId, UpsertUserWordRequest request) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy từ vựng với id " + wordId));

        UserWord userWord = userWordRepository.findByUserIdAndWordId(userId, wordId)
                .orElseGet(() -> UserWord.builder()
                        .user(userRepository.getReferenceById(userId))
                        .word(word)
                        .learnedAt(Instant.now())
                        .build());

        userWord.setStatus(request.status());
        userWord.setNote(normalizeNote(request.note()));
        UserWord saved = userWordRepository.save(userWord);
        // word đã được nạp ở trên nên mapper đọc trực tiếp, không sinh truy vấn phụ.
        saved.setWord(word);
        return userWordMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public int bulkUpsert(Long userId, List<Long> wordIds, UserWordStatus status) {
        if (wordIds == null || wordIds.isEmpty()) {
            return 0;
        }
        // Khử trùng lặp và bỏ null nhưng giữ thứ tự client gửi lên.
        Set<Long> requestedIds = new LinkedHashSet<>();
        for (Long id : wordIds) {
            if (id != null) {
                requestedIds.add(id);
            }
        }
        if (requestedIds.isEmpty()) {
            return 0;
        }

        // Chỉ tạo bản ghi cho từ thật sự tồn tại và chưa có trong sổ (idempotent).
        Set<Long> existingInNotebook = new HashSet<>(
                userWordRepository.findExistingWordIds(userId, requestedIds));
        List<Word> words = wordRepository.findAllById(requestedIds);

        UserWordStatus effectiveStatus = status != null ? status : UserWordStatus.LEARNED;
        Instant now = Instant.now();
        User userRef = userRepository.getReferenceById(userId);
        List<UserWord> toCreate = new ArrayList<>();
        for (Word word : words) {
            if (existingInNotebook.contains(word.getId())) {
                continue;
            }
            toCreate.add(UserWord.builder()
                    .user(userRef)
                    .word(word)
                    .status(effectiveStatus)
                    .learnedAt(now)
                    .build());
        }
        if (!toCreate.isEmpty()) {
            userWordRepository.saveAll(toCreate);
        }
        return toCreate.size();
    }

    @Override
    @Transactional
    public void remove(Long userId, Long wordId) {
        UserWord userWord = userWordRepository.findByUserIdAndWordId(userId, wordId)
                .orElseThrow(() -> new NotFoundException(
                        "Từ với id " + wordId + " chưa có trong sổ từ đã học của bạn"));
        userWordRepository.delete(userWord);
    }

    @Override
    public UserWordStatsResponse stats(Long userId) {
        long total = userWordRepository.countByUserId(userId);

        // Luôn trả đủ 3 trạng thái để client không phải kiểm tra khoá thiếu.
        Map<UserWordStatus, Long> byStatus = new EnumMap<>(UserWordStatus.class);
        for (UserWordStatus s : UserWordStatus.values()) {
            byStatus.put(s, 0L);
        }
        for (UserWordRepository.StatusCount row : userWordRepository.countGroupByStatus(userId)) {
            if (row.getStatus() != null) {
                byStatus.put(row.getStatus(), row.getTotal());
            }
        }

        // TreeMap để cấp HSK ra theo thứ tự tăng dần trong JSON.
        Map<Integer, Long> byHskLevel = new TreeMap<>();
        for (UserWordRepository.LevelCount row : userWordRepository.countGroupByHskLevel(userId)) {
            if (row.getHskLevel() != null) {
                byHskLevel.put(row.getHskLevel(), row.getTotal());
            }
        }

        Instant weekAgo = Instant.now().minus(WEEK_DAYS, ChronoUnit.DAYS);
        long learnedThisWeek = userWordRepository.countByUserIdAndLearnedAtGreaterThanEqual(userId, weekAgo);

        return new UserWordStatsResponse(total, byStatus, byHskLevel, learnedThisWeek);
    }

    @Override
    public List<Long> learnedWordIds(Long userId) {
        return userWordRepository.findWordIdsByUserId(userId);
    }

    // ------------------------------------------------------------------
    // Hàm hỗ trợ
    // ------------------------------------------------------------------

    /** Chuỗi trống coi như không lọc. */
    private static String normalizeQuery(String q) {
        if (q == null) {
            return null;
        }
        String trimmed = q.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /** Ghi chú trống lưu thành NULL cho gọn. */
    private static String normalizeNote(String note) {
        if (note == null) {
            return null;
        }
        String trimmed = note.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Áp thứ tự mặc định khi client không chỉ định sort.
     * Giữ nguyên {@code Pageable.unpaged()} (module sinh câu dùng để lấy toàn bộ sổ từ).
     */
    private static Pageable withDefaultSort(Pageable pageable) {
        if (pageable == null) {
            return Pageable.unpaged(DEFAULT_SORT);
        }
        if (pageable.getSort().isSorted()) {
            return pageable;
        }
        if (pageable.isUnpaged()) {
            return Pageable.unpaged(DEFAULT_SORT);
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DEFAULT_SORT);
    }
}
