package com.example.bewebtiengtrung.module.sentence.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.common.exception.BadRequestException;
import com.example.bewebtiengtrung.common.exception.ForbiddenException;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.ai.dto.GeneratedSentence;
import com.example.bewebtiengtrung.module.ai.dto.LearnedWordBrief;
import com.example.bewebtiengtrung.module.ai.service.AiSentenceGenerator;
import com.example.bewebtiengtrung.module.sentence.dto.AiStatusResponse;
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
import com.example.bewebtiengtrung.module.userword.service.UserWordService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cài đặt nghiệp vụ "Câu của tôi".
 *
 * <p>Điểm mấu chốt là pipeline {@link #generate}: mọi câu AI trả về đều phải qua bộ lọc
 * {@link SentenceText#unknownChars} (chỉ dùng chữ đã học), chống trùng theo {@code hanzi_key} và chống
 * "đổi chỗ" theo {@link SentenceText#bagKey} (cùng bộ chữ với câu đã có thì không phải câu mới);
 * nếu sau lọc còn dưới 60% số câu yêu cầu thì gọi AI thêm đúng MỘT lần nữa.</p>
 */
@Service
@Transactional(readOnly = true)
public class SentenceServiceImpl implements SentenceService {

    private static final Logger log = LoggerFactory.getLogger(SentenceServiceImpl.class);

    /** Phải có ít nhất chừng này từ đã học mới sinh câu được. */
    static final int MIN_LEARNED_WORDS = 5;

    /** Số câu bị loại tối đa đưa vào {@code rejectedSamples}. */
    static final int MAX_REJECTED_SAMPLES = 5;

    /** Dưới ngưỡng này (phần trăm của {@code count}) thì gọi AI thêm một lần. */
    static final int RETRY_THRESHOLD_PERCENT = 60;

    /** Tối đa số lần gọi AI trong một đợt. */
    static final int MAX_AI_CALLS = 2;

    private static final String AI_DISABLED_MESSAGE = "Chưa cấu hình ANTHROPIC_API_KEY trên máy chủ";

    private static final int MAX_HANZI = 200;
    private static final int MAX_PINYIN = 400;
    private static final int MAX_MEANING = 500;

    private final UserSentenceRepository sentenceRepository;
    private final SentenceMapper sentenceMapper;
    private final UserWordService userWordService;
    private final AiSentenceGenerator aiGenerator;

    /** Tham chiếu tới entity User của module khác mà không phụ thuộc repository của module đó. */
    private final EntityManager entityManager;

    /** Mở giao dịch ngắn CHỈ cho bước lưu của {@link #generate} (không giữ kết nối DB trong lúc chờ AI). */
    private final TransactionTemplate transactionTemplate;

    public SentenceServiceImpl(UserSentenceRepository sentenceRepository,
                               SentenceMapper sentenceMapper,
                               UserWordService userWordService,
                               AiSentenceGenerator aiGenerator,
                               EntityManager entityManager,
                               TransactionTemplate transactionTemplate) {
        this.sentenceRepository = sentenceRepository;
        this.sentenceMapper = sentenceMapper;
        this.userWordService = userWordService;
        this.aiGenerator = aiGenerator;
        this.entityManager = entityManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public List<SentenceResponse> listAll(Long userId) {
        return sentenceRepository.findByUserIdOrderByLevelAscIdAsc(userId).stream()
                .map(sentenceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BulkSentenceResponse addBulk(Long userId, BulkSentenceRequest request, SentenceSource source) {
        Set<String> seenKeys = new HashSet<>(sentenceRepository.findHanziKeysByUserId(userId));
        User userRef = entityManager.getReference(User.class, userId);
        Instant now = Instant.now();

        List<UserSentence> toSave = new ArrayList<>();
        int duplicates = 0;
        int index = 0;
        for (SentenceInput input : request.sentences()) {
            index++;
            String hanzi = input.hanzi().trim();
            String key = SentenceText.key(hanzi);
            if (key.isEmpty()) {
                throw new BadRequestException("Câu thứ " + index + " không có chữ Hán nào: \"" + hanzi + "\"");
            }
            if (!seenKeys.add(key)) {
                duplicates++;
                continue;
            }
            toSave.add(UserSentence.builder()
                    .user(userRef)
                    .userId(userId) // gán kèm bản sao chỉ-đọc để đối tượng trong bộ nhớ nhất quán (như Deck.ownerId)
                    .hanzi(hanzi)
                    .pinyin(input.pinyin().trim())
                    .meaningVi(input.meaningVi().trim())
                    .level(input.level() == null ? 1 : input.level())
                    .source(source == null ? SentenceSource.MANUAL : source)
                    .hanziKey(key)
                    .createdAt(now)
                    .build());
        }

        List<UserSentence> saved = sentenceRepository.saveAll(toSave);
        List<SentenceResponse> responses = saved.stream().map(sentenceMapper::toResponse).toList();
        return new BulkSentenceResponse(saved.size(), duplicates, responses);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        UserSentence sentence = sentenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu với id " + id));
        if (!userId.equals(sentence.getUserId())) {
            throw new ForbiddenException("Bạn không phải chủ sở hữu câu này");
        }
        sentenceRepository.delete(sentence);
    }

    @Override
    @Transactional
    public int deleteBySource(Long userId, SentenceSource source) {
        if (source == null) {
            throw new BadRequestException("Phải chỉ rõ nguồn câu cần xoá (MANUAL hoặc AI)");
        }
        if (source == SentenceSource.BUILTIN) {
            throw new BadRequestException("Không thể xoá các câu có sẵn của hệ thống");
        }
        return sentenceRepository.deleteByUserIdAndSource(userId, source);
    }

    @Override
    public AiStatusResponse aiStatus() {
        boolean enabled = aiGenerator.isEnabled();
        // Router biết provider nào đang chạy và vì sao tắt; generator giả trong test thì không.
        String provider = "none";
        String reason = enabled ? null : AI_DISABLED_MESSAGE;
        if (aiGenerator instanceof com.example.bewebtiengtrung.module.ai.service.AiSentenceGeneratorRouter router) {
            provider = router.activeProvider().name().toLowerCase(java.util.Locale.ROOT);
            reason = router.disabledReason();
        } else if (enabled) {
            provider = "custom";
        }
        return new AiStatusResponse(enabled, provider, aiGenerator.model(), reason);
    }

    // ------------------------------------------------------------------
    // Sinh câu bằng AI
    // ------------------------------------------------------------------

    /**
     * Gọi AI có thể mất tới hàng chục giây, nên KHÔNG giữ giao dịch (và kết nối DB trong pool nhỏ)
     * suốt quá trình: {@code NOT_SUPPORTED} treo giao dịch của lớp, các truy vấn đọc tự mở giao dịch ngắn
     * của repository, còn bước lưu chạy trong {@link #transactionTemplate}.
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GenerateSentencesResponse generate(Long userId, GenerateSentencesRequest request) {
        if (!aiGenerator.isEnabled()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI_DISABLED", AI_DISABLED_MESSAGE);
        }

        // 1. Từ đã học (toàn bộ, không phân trang).
        PageResponse<UserWordResponse> page =
                userWordService.list(userId, null, null, null, Pageable.unpaged());
        List<LearnedWordBrief> words = page.content().stream()
                .map(w -> new LearnedWordBrief(w.simplified(), w.pinyin(), w.meaningVi()))
                .toList();
        if (words.size() < MIN_LEARNED_WORDS) {
            throw new BadRequestException("Cần ít nhất " + MIN_LEARNED_WORDS + " từ đã học");
        }
        Set<Integer> learned = SentenceText.learnedCodePoints(
                words.stream().map(LearnedWordBrief::hanzi).toList());

        // 2. Câu đã có: khoá để chống trùng, túi chữ để chống "đổi chỗ", chữ Hán để gửi kèm "đừng tạo lại".
        Set<String> seenKeys = new HashSet<>(sentenceRepository.findHanziKeysByUserId(userId));
        Set<String> seenBags = new HashSet<>();
        for (String key : seenKeys) {
            seenBags.add(SentenceText.bagKey(key));
        }
        List<String> avoidHanzi = new ArrayList<>(sentenceRepository.findHanziByUserId(userId));

        int count = request.count();
        List<GeneratedSentence> accepted = new ArrayList<>();
        int rejected = 0;
        int duplicates = 0;
        int reordered = 0;
        List<String> rejectedSamples = new ArrayList<>();

        // 3–5. Gọi AI, lọc; gọi lại tối đa một lần nếu còn thiếu nhiều.
        int calls = 0;
        while (calls < MAX_AI_CALLS && accepted.size() < count) {
            calls++;
            int missing = count - accepted.size();
            List<GeneratedSentence> candidates =
                    aiGenerator.generate(words, avoidHanzi, missing, request.level(), request.focusWords());

            for (GeneratedSentence candidate : candidates) {
                if (accepted.size() >= count) {
                    break; // đủ rồi, phần dư xin thêm để bù chỉ dùng khi cần
                }
                String hanzi = candidate.hanzi() == null ? "" : candidate.hanzi().trim();
                String key = SentenceText.key(hanzi);
                if (!isWellFormed(candidate, hanzi, key)) {
                    rejected++;
                    addSample(rejectedSamples, hanzi.isEmpty() ? "(trống)" : hanzi, "thiếu dữ liệu");
                    continue;
                }
                List<String> unknown = SentenceText.unknownChars(hanzi, learned);
                if (!unknown.isEmpty()) {
                    rejected++;
                    addSample(rejectedSamples, hanzi, "lạ: " + String.join("", unknown));
                    continue;
                }
                if (seenKeys.contains(key)) {
                    duplicates++;
                    continue;
                }
                // Cùng bộ chữ với một câu đã có (hoặc câu vừa nhận trong đợt) = câu cũ đổi chỗ, không phải câu mới.
                if (!seenBags.add(SentenceText.bagKey(key))) {
                    reordered++;
                    addSample(rejectedSamples, hanzi, "đổi chỗ câu đã có");
                    continue;
                }
                seenKeys.add(key);
                accepted.add(candidate);
                avoidHanzi.add(hanzi);
            }

            boolean enough = accepted.size() * 100L >= (long) count * RETRY_THRESHOLD_PERCENT;
            if (enough) {
                break;
            }
            if (calls < MAX_AI_CALLS) {
                log.info("Sau lần gọi {} chỉ có {}/{} câu hợp lệ — gọi AI thêm một lần", calls, accepted.size(), count);
            }
        }

        // 6. Lưu các câu hợp lệ với source = AI — trong một giao dịch ngắn riêng. Nếu người dùng muốn
        //    THAY bộ AI cũ thì xoá nó ngay trước khi lưu, cùng giao dịch: hoặc có bộ mới thế chỗ, hoặc
        //    không mất gì. Đợt mới rỗng thì giữ nguyên bộ cũ — người học còn câu để nghe trong lúc thử lại.
        final Integer requestedLevel = request.level();
        final List<GeneratedSentence> toSave = List.copyOf(accepted);
        final boolean replace = request.wantsReplace() && !toSave.isEmpty();
        SaveOutcome outcome = transactionTemplate.execute(status -> {
            int replaced = replace ? sentenceRepository.deleteByUserIdAndSource(userId, SentenceSource.AI) : 0;
            User userRef = entityManager.getReference(User.class, userId);
            Instant now = Instant.now();
            List<UserSentence> entities = new ArrayList<>(toSave.size());
            for (GeneratedSentence candidate : toSave) {
                String hanzi = candidate.hanzi().trim();
                String key = SentenceText.key(hanzi);
                entities.add(UserSentence.builder()
                        .user(userRef)
                        .userId(userId) // gán kèm bản sao chỉ-đọc để đối tượng trong bộ nhớ nhất quán (như Deck.ownerId)
                        .hanzi(hanzi)
                        .pinyin(candidate.pinyin().trim())
                        .meaningVi(candidate.vi().trim())
                        .level(resolveLevel(requestedLevel, candidate.level(), key))
                        .source(SentenceSource.AI)
                        .hanziKey(key)
                        .createdAt(now)
                        .build());
            }
            return new SaveOutcome(sentenceRepository.saveAll(entities), replaced);
        });
        List<UserSentence> saved = outcome == null || outcome.saved() == null ? List.of() : outcome.saved();
        int replaced = outcome == null ? 0 : outcome.replaced();
        List<SentenceResponse> responses = saved.stream().map(sentenceMapper::toResponse).toList();

        log.info("AI sinh câu cho user {}: yêu cầu {}, lưu {}, loại {} (chữ lạ), trùng {}, đổi chỗ {}, "
                        + "bỏ {} câu AI cũ, {} lần gọi",
                userId, count, saved.size(), rejected, duplicates, reordered, replaced, calls);
        return new GenerateSentencesResponse(count, saved.size(), rejected, duplicates, reordered, replaced,
                responses, List.copyOf(rejectedSamples), aiGenerator.model());
    }

    /** Kết quả bước lưu của {@link #generate}: các câu vừa ghi và số câu AI cũ đã bỏ. */
    private record SaveOutcome(List<UserSentence> saved, int replaced) {
    }

    /** Ứng viên phải có đủ ba trường, có chữ Hán và không vượt độ dài cột. */
    private static boolean isWellFormed(GeneratedSentence candidate, String hanzi, String key) {
        if (key.isEmpty() || hanzi.length() > MAX_HANZI) {
            return false;
        }
        String pinyin = candidate.pinyin();
        String vi = candidate.vi();
        return pinyin != null && !pinyin.isBlank() && pinyin.trim().length() <= MAX_PINYIN
                && vi != null && !vi.isBlank() && vi.trim().length() <= MAX_MEANING;
    }

    /** Thêm mẫu câu bị loại (tối đa {@link #MAX_REJECTED_SAMPLES}) dạng "他很高兴。 (lạ: 高兴)". */
    private static void addSample(List<String> samples, String hanzi, String reason) {
        if (samples.size() < MAX_REJECTED_SAMPLES) {
            samples.add(hanzi + " (" + reason + ")");
        }
    }

    /**
     * Cấp độ để lưu: người dùng chỉ định cấp thì dùng cấp đó; không thì lấy cấp AI trả về nếu hợp lệ (1..3);
     * còn lại suy từ số chữ Hán (≤5 → 1, ≤7 → 2, còn lại → 3).
     */
    static int resolveLevel(Integer requested, int candidateLevel, String key) {
        if (requested != null && requested >= 1 && requested <= 3) {
            return requested;
        }
        if (candidateLevel >= 1 && candidateLevel <= 3) {
            return candidateLevel;
        }
        int length = key.codePointCount(0, key.length());
        if (length <= 5) {
            return 1;
        }
        return length <= 7 ? 2 : 3;
    }
}
