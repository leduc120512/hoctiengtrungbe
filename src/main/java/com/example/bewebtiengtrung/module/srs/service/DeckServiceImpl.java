package com.example.bewebtiengtrung.module.srs.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.common.exception.ConflictException;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.srs.dto.DeckRequest;
import com.example.bewebtiengtrung.module.srs.dto.DeckResponse;
import com.example.bewebtiengtrung.module.srs.dto.FlashcardRequest;
import com.example.bewebtiengtrung.module.srs.dto.FlashcardResponse;
import com.example.bewebtiengtrung.module.srs.entity.Deck;
import com.example.bewebtiengtrung.module.srs.entity.Flashcard;
import com.example.bewebtiengtrung.module.srs.mapper.DeckMapper;
import com.example.bewebtiengtrung.module.srs.mapper.FlashcardMapper;
import com.example.bewebtiengtrung.module.srs.repository.DeckRepository;
import com.example.bewebtiengtrung.module.srs.repository.FlashcardRepository;
import com.example.bewebtiengtrung.module.srs.repository.ReviewLogRepository;
import com.example.bewebtiengtrung.module.srs.repository.ReviewStateRepository;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.vocabulary.entity.Topic;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.security.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

/** Cài đặt nghiệp vụ bộ thẻ / thẻ ghi nhớ. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeckServiceImpl implements DeckService {

    private static final int MAX_SLUG_LENGTH = 150;
    private static final int MAX_SLUG_BASE_LENGTH = 140;

    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;
    private final ReviewStateRepository reviewStateRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final DeckMapper deckMapper;
    private final FlashcardMapper flashcardMapper;
    private final DeckAccessPolicy accessPolicy;

    /**
     * Dùng EntityManager để lấy tham chiếu tới entity của module khác (user, vocabulary)
     * nhằm không phụ thuộc vào repository do module đó sở hữu.
     */
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PageResponse<DeckResponse> list(Integer hskLevel, Long topicId, Pageable pageable) {
        Long userId = SecurityUtils.currentUserIdOrEmpty().orElse(null);
        Page<DeckResponse> page = deckRepository.findVisible(userId, hskLevel, topicId, pageable)
                .map(deckMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    public DeckResponse get(Long deckId) {
        Deck deck = loadDeck(deckId);
        accessPolicy.requireRead(deck, SecurityUtils.currentUserIdOrEmpty().orElse(null));
        return deckMapper.toResponse(deck);
    }

    @Override
    @Transactional
    public DeckResponse create(DeckRequest request) {
        Long userId = SecurityUtils.currentUserId();

        Deck deck = new Deck();
        // Gán cả quan hệ lẫn khoá ngoại chỉ-đọc để đối tượng trong bộ nhớ luôn nhất quán.
        deck.setOwner(entityManager.getReference(User.class, userId));
        deck.setOwnerId(userId);
        applyTopic(deck, request.topicId());
        deck.setName(request.name().trim());
        deck.setDescription(request.description());
        deck.setHskLevel(request.hskLevel());
        deck.setIsPublic(Boolean.TRUE.equals(request.isPublic()));
        // Deck do người dùng tạo không bao giờ là deck hệ thống.
        deck.setIsSystem(Boolean.FALSE);
        deck.setCardCount(0);
        deck.setSlug(resolveSlug(request.slug(), request.name(), null));

        return deckMapper.toResponse(deckRepository.save(deck));
    }

    @Override
    @Transactional
    public DeckResponse update(Long deckId, DeckRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Deck deck = loadDeck(deckId);
        accessPolicy.requireWrite(deck, userId);

        deck.setName(request.name().trim());
        deck.setDescription(request.description());
        deck.setHskLevel(request.hskLevel());
        applyTopic(deck, request.topicId());
        if (request.isPublic() != null) {
            deck.setIsPublic(request.isPublic());
        }
        deck.setSlug(resolveSlug(request.slug(), request.name(), deck.getId()));

        return deckMapper.toResponse(deckRepository.save(deck));
    }

    @Override
    @Transactional
    public void delete(Long deckId) {
        Long userId = SecurityUtils.currentUserId();
        Deck deck = loadDeck(deckId);
        accessPolicy.requireWrite(deck, userId);

        // Dọn dữ liệu ôn tập của mọi người dùng trước, sau đó xoá deck
        // (cascade ALL + orphanRemoval sẽ xoá các flashcard con).
        reviewLogRepository.deleteByDeckId(deckId);
        reviewStateRepository.deleteByDeckId(deckId);
        deckRepository.delete(deck);
    }

    @Override
    public PageResponse<FlashcardResponse> listCards(Long deckId, Pageable pageable) {
        Deck deck = loadDeck(deckId);
        accessPolicy.requireRead(deck, SecurityUtils.currentUserIdOrEmpty().orElse(null));
        Page<FlashcardResponse> page = flashcardRepository.findPageByDeckId(deckId, pageable)
                .map(flashcardMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    @Transactional
    public FlashcardResponse addCard(Long deckId, FlashcardRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Deck deck = loadDeck(deckId);
        accessPolicy.requireWrite(deck, userId);

        Flashcard card = flashcardMapper.toEntity(request);
        card.setDeck(deck);
        card.setDeckId(deck.getId());
        applyWord(card, request.wordId());
        if (request.sortOrder() == null) {
            // Mặc định thẻ mới nằm ở cuối bộ thẻ.
            card.setSortOrder(deck.getCardCount() != null ? deck.getCardCount() : 0);
        }
        Flashcard saved = flashcardRepository.saveAndFlush(card);

        syncCardCount(deck);
        return flashcardMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public FlashcardResponse updateCard(Long cardId, FlashcardRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Flashcard card = loadCard(cardId);
        accessPolicy.requireWrite(card.getDeck(), userId);

        Integer previousSortOrder = card.getSortOrder();
        flashcardMapper.applyRequest(card, request);
        if (request.sortOrder() == null) {
            // Không gửi sortOrder thì giữ nguyên thứ tự cũ.
            card.setSortOrder(previousSortOrder != null ? previousSortOrder : 0);
        }
        applyWord(card, request.wordId());

        return flashcardMapper.toResponse(flashcardRepository.save(card));
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        Long userId = SecurityUtils.currentUserId();
        Flashcard card = loadCard(cardId);
        Deck deck = card.getDeck();
        accessPolicy.requireWrite(deck, userId);

        // Xoá dữ liệu ôn tập tham chiếu tới thẻ trước khi xoá thẻ.
        reviewLogRepository.deleteByFlashcardId(cardId);
        reviewStateRepository.deleteByFlashcardId(cardId);
        flashcardRepository.delete(card);
        flashcardRepository.flush();

        syncCardCount(deck);
    }

    // ------------------------------------------------------------------
    // Hàm hỗ trợ
    // ------------------------------------------------------------------

    private Deck loadDeck(Long deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bộ thẻ với id " + deckId));
    }

    private Flashcard loadCard(Long cardId) {
        return flashcardRepository.findDetailById(cardId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thẻ với id " + cardId));
    }

    /** Đồng bộ lại số thẻ của deck sau khi thêm hoặc xoá thẻ. */
    private void syncCardCount(Deck deck) {
        deck.setCardCount((int) flashcardRepository.countByDeckId(deck.getId()));
        deckRepository.save(deck);
    }

    /** Gán chủ đề cho deck (kèm khoá ngoại chỉ-đọc), ném 404 nếu id không tồn tại. */
    private void applyTopic(Deck deck, Long topicId) {
        if (topicId == null) {
            deck.setTopic(null);
            deck.setTopicId(null);
            return;
        }
        Topic topic = entityManager.find(Topic.class, topicId);
        if (topic == null) {
            throw new NotFoundException("Không tìm thấy chủ đề với id " + topicId);
        }
        deck.setTopic(topic);
        deck.setTopicId(topicId);
    }

    /** Gán từ vựng cho thẻ (kèm khoá ngoại chỉ-đọc), ném 404 nếu id không tồn tại. */
    private void applyWord(Flashcard card, Long wordId) {
        if (wordId == null) {
            card.setWord(null);
            card.setWordId(null);
            return;
        }
        Word word = entityManager.find(Word.class, wordId);
        if (word == null) {
            throw new NotFoundException("Không tìm thấy từ vựng với id " + wordId);
        }
        card.setWord(word);
        card.setWordId(wordId);
    }

    /**
     * Sinh slug duy nhất. Nếu client tự chỉ định slug mà bị trùng thì báo lỗi 409;
     * nếu slug được sinh tự động từ tên thì thêm hậu tố số cho tới khi không trùng.
     */
    private String resolveSlug(String requestedSlug, String name, Long currentDeckId) {
        boolean explicit = requestedSlug != null && !requestedSlug.isBlank();
        String base = slugify(explicit ? requestedSlug : name);
        if (base.isEmpty()) {
            base = "deck";
        }
        if (base.length() > MAX_SLUG_BASE_LENGTH) {
            base = base.substring(0, MAX_SLUG_BASE_LENGTH);
        }

        if (explicit) {
            if (isSlugTaken(base, currentDeckId)) {
                throw new ConflictException("Slug đã được sử dụng: " + base);
            }
            return base;
        }

        String candidate = base;
        int suffix = 2;
        while (isSlugTaken(candidate, currentDeckId)) {
            if (suffix > 999) {
                candidate = base + "-" + UUID.randomUUID().toString().substring(0, 8);
                break;
            }
            candidate = base + "-" + suffix;
            suffix++;
        }
        return candidate.length() > MAX_SLUG_LENGTH ? candidate.substring(0, MAX_SLUG_LENGTH) : candidate;
    }

    private boolean isSlugTaken(String slug, Long currentDeckId) {
        return currentDeckId == null
                ? deckRepository.existsBySlug(slug)
                : deckRepository.existsBySlugAndIdNot(slug, currentDeckId);
    }

    /** Chuẩn hoá chuỗi thành slug: bỏ dấu tiếng Việt, thay ký tự lạ bằng dấu gạch ngang. */
    private String slugify(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");
    }
}
