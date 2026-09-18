package com.example.bewebtiengtrung.module.vocabulary.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.common.exception.BadRequestException;
import com.example.bewebtiengtrung.common.exception.ConflictException;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.vocabulary.dto.CreateWordExampleRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.CreateWordRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.UpdateWordRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordDetailResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordExampleResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordSummaryResponse;
import com.example.bewebtiengtrung.module.vocabulary.entity.Topic;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.module.vocabulary.entity.WordExample;
import com.example.bewebtiengtrung.module.vocabulary.mapper.WordMapper;
import com.example.bewebtiengtrung.module.vocabulary.repository.TopicRepository;
import com.example.bewebtiengtrung.module.vocabulary.repository.WordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Cài đặt nghiệp vụ quản lý từ vựng. Chỉ trả về DTO, không trả entity ra ngoài.
 */
@Service
@Transactional(readOnly = true)
public class WordServiceImpl implements WordService {

    /** Số từ tối đa cho một lần lấy ngẫu nhiên. */
    private static final int MAX_RANDOM_COUNT = 100;

    private final WordRepository wordRepository;
    private final TopicRepository topicRepository;
    private final WordMapper wordMapper;

    public WordServiceImpl(WordRepository wordRepository,
                           TopicRepository topicRepository,
                           WordMapper wordMapper) {
        this.wordRepository = wordRepository;
        this.topicRepository = topicRepository;
        this.wordMapper = wordMapper;
    }

    @Override
    public PageResponse<WordSummaryResponse> search(String q, Integer hskLevel, Long topicId, Pageable pageable) {
        // Chuỗi rỗng được coi như không lọc, để truy vấn đi vào nhánh ":q IS NULL"
        String keyword = (q == null || q.isBlank()) ? null : q.trim();
        Page<Word> page = wordRepository.search(keyword, hskLevel, topicId, pageable);
        return PageResponse.of(page.map(wordMapper::toSummary));
    }

    @Override
    public WordDetailResponse findById(Long id) {
        Word word = wordRepository.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy từ vựng có id: " + id));
        return wordMapper.toDetail(word);
    }

    @Override
    public List<WordSummaryResponse> findRandom(int count, Integer hskLevel) {
        if (count < 1 || count > MAX_RANDOM_COUNT) {
            throw new BadRequestException("Tham số count phải nằm trong khoảng 1 đến " + MAX_RANDOM_COUNT);
        }
        return wordRepository.findRandomWords(hskLevel, count).stream()
                .map(wordMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WordDetailResponse create(CreateWordRequest request) {
        // Ràng buộc uk_words_simplified_pinyin: một từ được xác định bởi cặp (giản thể, pinyin)
        if (wordRepository.existsBySimplifiedAndPinyin(request.simplified(), request.pinyin())) {
            throw new ConflictException(buildDuplicateMessage(request.simplified(), request.pinyin()));
        }

        Word word = new Word();
        applyBasicFields(word, request.simplified(), request.traditional(), request.pinyin(),
                request.pinyinNumbered(), request.meaningVi(), request.meaningEn(),
                request.partOfSpeech(), request.hskLevel(), request.strokeCount(),
                request.frequencyRank(), request.audioUrl(), request.imageUrl(), request.note());
        word.replaceTopics(loadTopics(request.topicIds()));
        applyExamples(word, request.examples());

        Word saved = wordRepository.save(word);
        return wordMapper.toDetail(saved);
    }

    @Override
    @Transactional
    public WordDetailResponse update(Long id, UpdateWordRequest request) {
        Word word = wordRepository.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy từ vựng có id: " + id));

        if (wordRepository.existsBySimplifiedAndPinyinAndIdNot(request.simplified(), request.pinyin(), id)) {
            throw new ConflictException(buildDuplicateMessage(request.simplified(), request.pinyin()));
        }

        applyBasicFields(word, request.simplified(), request.traditional(), request.pinyin(),
                request.pinyinNumbered(), request.meaningVi(), request.meaningEn(),
                request.partOfSpeech(), request.hskLevel(), request.strokeCount(),
                request.frequencyRank(), request.audioUrl(), request.imageUrl(), request.note());

        // null nghĩa là giữ nguyên; khác null thì thay thế toàn bộ tập hiện có
        if (request.topicIds() != null) {
            word.replaceTopics(loadTopics(request.topicIds()));
        }
        if (request.examples() != null) {
            word.clearExamples();
            applyExamples(word, request.examples());
        }

        Word saved = wordRepository.save(word);
        return wordMapper.toDetail(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy từ vựng có id: " + id));
        // Xoá qua entity để cascade các câu ví dụ và gỡ liên kết trong bảng word_topics
        wordRepository.delete(word);
    }

    @Override
    @Transactional
    public WordExampleResponse addExample(Long wordId, CreateWordExampleRequest request) {
        Word word = wordRepository.findDetailById(wordId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy từ vựng có id: " + wordId));

        // Câu ví dụ mới phải nằm CUỐI danh sách: lấy sortOrder lớn nhất hiện có rồi cộng 1.
        // Không dùng size() vì admin có thể đã đặt sortOrder thưa (ví dụ 10, 20) khi tạo từ.
        int nextSortOrder = word.getExamples().stream()
                .map(WordExample::getSortOrder)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(-1) + 1;

        WordExample example = wordMapper.toExampleEntity(request, nextSortOrder);
        word.addExample(example);

        // Flush để lấy khoá chính do database sinh ra trước khi chuyển sang DTO
        wordRepository.saveAndFlush(word);
        return wordMapper.toExampleResponse(example);
    }

    /** Gán các trường vô hướng của từ vựng (dùng chung cho tạo mới và cập nhật). */
    private void applyBasicFields(Word word, String simplified, String traditional, String pinyin,
                                  String pinyinNumbered, String meaningVi, String meaningEn,
                                  String partOfSpeech, Integer hskLevel, Integer strokeCount,
                                  Integer frequencyRank, String audioUrl, String imageUrl, String note) {
        word.setSimplified(simplified);
        word.setTraditional(traditional);
        word.setPinyin(pinyin);
        word.setPinyinNumbered(pinyinNumbered);
        word.setMeaningVi(meaningVi);
        word.setMeaningEn(meaningEn);
        word.setPartOfSpeech(partOfSpeech);
        word.setHskLevel(hskLevel != null ? hskLevel : 1);
        word.setStrokeCount(strokeCount);
        word.setFrequencyRank(frequencyRank);
        word.setAudioUrl(audioUrl);
        word.setImageUrl(imageUrl);
        word.setNote(note);
    }

    /** Thông báo lỗi khi cặp (giản thể, pinyin) đã tồn tại. */
    private String buildDuplicateMessage(String simplified, String pinyin) {
        return "Từ vựng đã tồn tại với chữ giản thể [" + simplified + "] và pinyin [" + pinyin + "]";
    }

    /**
     * Nạp các chủ đề theo danh sách id và kiểm tra tất cả đều tồn tại.
     *
     * @return tập chủ đề, rỗng nếu {@code topicIds} là null hoặc rỗng
     */
    private Set<Topic> loadTopics(Set<Long> topicIds) {
        if (topicIds == null || topicIds.isEmpty()) {
            return new LinkedHashSet<>();
        }
        List<Topic> topics = topicRepository.findAllById(topicIds);
        if (topics.size() != topicIds.size()) {
            Set<Long> foundIds = topics.stream().map(Topic::getId).collect(Collectors.toSet());
            List<Long> missingIds = topicIds.stream()
                    .filter(topicId -> !foundIds.contains(topicId))
                    .collect(Collectors.toList());
            throw new NotFoundException("Không tìm thấy chủ đề có id: " + missingIds);
        }
        return new LinkedHashSet<>(topics);
    }

    /** Gắn danh sách câu ví dụ vào từ, tự sinh sortOrder khi client không gửi. */
    private void applyExamples(Word word, List<CreateWordExampleRequest> examples) {
        if (examples == null || examples.isEmpty()) {
            return;
        }
        List<CreateWordExampleRequest> items = new ArrayList<>(examples);
        for (int i = 0; i < items.size(); i++) {
            word.addExample(wordMapper.toExampleEntity(items.get(i), i));
        }
    }
}
