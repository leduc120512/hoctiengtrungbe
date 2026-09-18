package com.example.bewebtiengtrung.module.srs.service;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.srs.dto.ReviewCardResponse;
import com.example.bewebtiengtrung.module.srs.dto.ReviewRequest;
import com.example.bewebtiengtrung.module.srs.dto.ReviewStateResponse;
import com.example.bewebtiengtrung.module.srs.dto.SrsStatsResponse;
import com.example.bewebtiengtrung.module.srs.entity.Deck;
import com.example.bewebtiengtrung.module.srs.entity.Flashcard;
import com.example.bewebtiengtrung.module.srs.entity.ReviewLog;
import com.example.bewebtiengtrung.module.srs.entity.ReviewState;
import com.example.bewebtiengtrung.module.srs.entity.ReviewStatus;
import com.example.bewebtiengtrung.module.srs.mapper.ReviewMapper;
import com.example.bewebtiengtrung.module.srs.repository.DeckRepository;
import com.example.bewebtiengtrung.module.srs.repository.FlashcardRepository;
import com.example.bewebtiengtrung.module.srs.repository.ReviewLogRepository;
import com.example.bewebtiengtrung.module.srs.repository.ReviewStateRepository;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.security.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Cài đặt nghiệp vụ ôn tập giãn cách theo thuật toán SM-2. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SrsServiceImpl implements SrsService {

    /** Giới hạn tối đa số thẻ trả về cho một phiên ôn. */
    private static final int MAX_DUE_LIMIT = 200;

    private static final int DEFAULT_DUE_LIMIT = 20;

    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;
    private final ReviewStateRepository reviewStateRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final ReviewMapper reviewMapper;
    private final Sm2Calculator sm2Calculator;
    private final DeckAccessPolicy accessPolicy;

    /** Tham chiếu tới entity User của module khác mà không phụ thuộc repository của module đó. */
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public MessageResponse subscribe(Long deckId) {
        Long userId = SecurityUtils.currentUserId();
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bộ thẻ với id " + deckId));
        accessPolicy.requireRead(deck, userId);

        List<Long> cardIds = flashcardRepository.findIdsByDeckId(deckId);
        if (cardIds.isEmpty()) {
            return new MessageResponse("Bộ thẻ chưa có thẻ nào để đăng ký ôn tập");
        }

        // Idempotent: chỉ tạo trạng thái cho những thẻ người dùng chưa có.
        Set<Long> existingCardIds = new HashSet<>(
                reviewStateRepository.findExistingFlashcardIds(userId, cardIds));

        Instant now = Instant.now();
        User userRef = entityManager.getReference(User.class, userId);
        List<ReviewState> newStates = new ArrayList<>();
        for (Long cardId : cardIds) {
            if (existingCardIds.contains(cardId)) {
                continue;
            }
            ReviewState state = ReviewState.builder()
                    .user(userRef)
                    .flashcard(flashcardRepository.getReferenceById(cardId))
                    .status(ReviewStatus.NEW)
                    .easeFactor(Sm2Calculator.DEFAULT_EASE_FACTOR)
                    .intervalDays(0)
                    .repetitions(0)
                    .lapses(0)
                    .dueAt(now)
                    .build();
            newStates.add(state);
        }
        reviewStateRepository.saveAll(newStates);

        return new MessageResponse("Đã thêm " + newStates.size() + " thẻ mới vào danh sách ôn tập"
                + " (bộ thẻ có tổng cộng " + cardIds.size() + " thẻ)");
    }

    @Override
    public List<ReviewCardResponse> getDue(Long deckId, int limit) {
        Long userId = SecurityUtils.currentUserId();
        int safeLimit = normalizeLimit(limit);
        List<ReviewState> dueStates = reviewStateRepository.findDue(
                userId, Instant.now(), deckId, PageRequest.of(0, safeLimit));

        List<ReviewCardResponse> result = new ArrayList<>(dueStates.size());
        for (ReviewState state : dueStates) {
            result.add(reviewMapper.toReviewCard(state));
        }
        return result;
    }

    @Override
    @Transactional
    public ReviewStateResponse review(ReviewRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Flashcard card = flashcardRepository.findDetailById(request.flashcardId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thẻ với id " + request.flashcardId()));
        // Chỉ được ôn thẻ thuộc bộ thẻ mà người dùng có quyền xem.
        accessPolicy.requireRead(card.getDeck(), userId);

        Instant now = Instant.now();
        ReviewState state = reviewStateRepository.findByUserIdAndFlashcardId(userId, card.getId())
                .orElseGet(() -> createInitialState(userId, card, now));

        int previousInterval = state.getIntervalDays() != null ? state.getIntervalDays() : 0;
        double previousEaseFactor = state.getEaseFactor() != null
                ? state.getEaseFactor()
                : Sm2Calculator.DEFAULT_EASE_FACTOR;
        int previousRepetitions = state.getRepetitions() != null ? state.getRepetitions() : 0;
        int previousLapses = state.getLapses() != null ? state.getLapses() : 0;

        Sm2Calculator.Sm2Result result = sm2Calculator.apply(
                previousEaseFactor, previousInterval, previousRepetitions, previousLapses,
                request.rating(), now);

        state.setEaseFactor(result.easeFactor());
        state.setIntervalDays(result.intervalDays());
        state.setRepetitions(result.repetitions());
        state.setLapses(result.lapses());
        state.setStatus(result.status());
        state.setDueAt(result.dueAt());
        state.setLastReviewedAt(now);
        ReviewState savedState = reviewStateRepository.save(state);

        // Luôn ghi lại nhật ký để phục vụ thống kê và chuỗi ngày học.
        ReviewLog log = ReviewLog.builder()
                .user(entityManager.getReference(User.class, userId))
                .flashcard(card)
                .rating(request.rating())
                .previousInterval(previousInterval)
                .newInterval(result.intervalDays())
                .easeFactorAfter(result.easeFactor())
                .reviewedAt(now)
                .build();
        reviewLogRepository.save(log);

        return reviewMapper.toResponse(savedState);
    }

    @Override
    public SrsStatsResponse stats() {
        Long userId = SecurityUtils.currentUserId();

        // Mọi mốc ngày đều tính theo UTC cho khớp với hibernate.jdbc.time_zone=UTC.
        LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC);
        Instant startOfToday = today.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant startOfTomorrow = today.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        long newCount = reviewStateRepository.countByUserIdAndStatus(userId, ReviewStatus.NEW);
        // Thẻ "đang học" gồm cả thẻ mới học lần đầu và thẻ vừa quên phải học lại.
        long learningCount = reviewStateRepository.countByUserIdAndStatus(userId, ReviewStatus.LEARNING)
                + reviewStateRepository.countByUserIdAndStatus(userId, ReviewStatus.RELEARNING);
        long reviewCount = reviewStateRepository.countByUserIdAndStatus(userId, ReviewStatus.REVIEW);
        long dueToday = reviewStateRepository.countByUserIdAndDueAtLessThan(userId, startOfTomorrow);
        long reviewedToday = reviewLogRepository.countInRange(userId, startOfToday, startOfTomorrow);
        int streakDays = calculateStreak(reviewLogRepository.findRecentReviewDates(userId), today);

        return new SrsStatsResponse(newCount, learningCount, reviewCount, dueToday, reviewedToday, streakDays);
    }

    // ------------------------------------------------------------------
    // Hàm hỗ trợ
    // ------------------------------------------------------------------

    /**
     * Đếm số ngày học liên tiếp tính ngược từ hôm nay.
     * Nếu hôm nay chưa ôn thẻ nào thì chuỗi bằng 0.
     *
     * @param reviewDates danh sách ngày dạng {@code yyyy-MM-dd} (UTC) lấy từ nhật ký
     * @param today       ngày hôm nay theo UTC
     */
    static int calculateStreak(List<String> reviewDates, LocalDate today) {
        if (reviewDates == null || reviewDates.isEmpty()) {
            return 0;
        }
        Set<LocalDate> days = new HashSet<>();
        for (String raw : reviewDates) {
            if (raw != null && !raw.isBlank()) {
                days.add(LocalDate.parse(raw.trim()));
            }
        }

        int streak = 0;
        LocalDate cursor = today;
        while (days.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    /** Trạng thái khởi tạo cho thẻ chưa từng được ôn (chưa đăng ký deck). */
    private ReviewState createInitialState(Long userId, Flashcard card, Instant now) {
        return ReviewState.builder()
                .user(entityManager.getReference(User.class, userId))
                .flashcard(card)
                .status(ReviewStatus.NEW)
                .easeFactor(Sm2Calculator.DEFAULT_EASE_FACTOR)
                .intervalDays(0)
                .repetitions(0)
                .lapses(0)
                .dueAt(now)
                .build();
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_DUE_LIMIT;
        }
        return Math.min(limit, MAX_DUE_LIMIT);
    }
}
