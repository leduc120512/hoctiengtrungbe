package com.example.bewebtiengtrung.module.wordimport.service;

import com.example.bewebtiengtrung.common.exception.BadRequestException;
import com.example.bewebtiengtrung.common.exception.ConflictException;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.dictionary.service.PinyinUtils;
import com.example.bewebtiengtrung.module.vocabulary.entity.Topic;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.module.vocabulary.repository.TopicRepository;
import com.example.bewebtiengtrung.module.vocabulary.repository.WordRepository;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmRow;
import com.example.bewebtiengtrung.module.wordimport.repository.ImportWordLookupRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Thực thi MỘT dòng confirm trong transaction riêng ({@code REQUIRES_NEW}).
 *
 * <p>Tách thành bean riêng (không đặt trong {@code ImportServiceImpl}) vì proxy transaction của Spring
 * không áp dụng cho lời gọi nội bộ (self-invocation): nếu {@code confirm} gọi thẳng một phương thức
 * {@code @Transactional} của chính nó thì annotation bị bỏ qua. Nhờ transaction riêng, một dòng lỗi
 * (trùng khoá, thiếu dữ liệu...) chỉ rollback dòng đó, các dòng khác vẫn được ghi.</p>
 */
@Component
public class ImportRowExecutor {

    private final WordRepository wordRepository;
    private final TopicRepository topicRepository;
    private final ImportWordLookupRepository lookupRepository;

    public ImportRowExecutor(WordRepository wordRepository, TopicRepository topicRepository,
                             ImportWordLookupRepository lookupRepository) {
        this.wordRepository = wordRepository;
        this.topicRepository = topicRepository;
        this.lookupRepository = lookupRepository;
    }

    /**
     * Thực thi một dòng theo {@code action}.
     *
     * @return id của từ liên quan (mới tạo, vừa cập nhật hoặc vừa liên kết); {@code null} khi SKIP/NEEDS_INPUT
     * @throws BadRequestException thiếu dữ liệu bắt buộc
     * @throws ConflictException   cặp (giản thể, pinyin) đã tồn tại
     * @throws NotFoundException   {@code existingWordId} hoặc chủ đề không tồn tại
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long execute(ImportConfirmRow row) {
        if (row.action() == null) {
            throw new BadRequestException("Thiếu hành động (action)");
        }
        switch (row.action()) {
            case CREATE:
                return create(row);
            case UPDATE:
                return update(row);
            case LINK:
                return link(row);
            case SKIP:
            case NEEDS_INPUT:
            default:
                return null;
        }
    }

    /** Tạo từ mới; kiểm tra đủ dữ liệu và chưa trùng cặp (giản thể, pinyin). */
    private Long create(ImportConfirmRow row) {
        String simplified = ImportText.trimToNull(row.simplified());
        // Cột words.pinyin luôn lưu dạng có dấu: người dùng sửa ô pinyin thành "xue2 xi2" thì vẫn đổi về "xué xí"
        String pinyin = ImportText.toMarkedPinyin(ImportText.trimToNull(row.pinyin()));
        String meaningVi = ImportText.trimToNull(row.meaningVi());
        if (simplified == null) {
            throw new BadRequestException("Thiếu chữ Hán");
        }
        if (pinyin == null) {
            throw new BadRequestException("Thiếu pinyin");
        }
        if (meaningVi == null) {
            throw new BadRequestException("Thiếu nghĩa tiếng Việt");
        }
        if (row.hskLevel() == null) {
            throw new BadRequestException("Thiếu cấp độ HSK");
        }
        if (wordRepository.existsBySimplifiedAndPinyin(simplified, pinyin)) {
            throw new ConflictException("Đã tồn tại: " + simplified + " [" + pinyin + "]");
        }
        requireNoSameReading(simplified, pinyin, null);

        String pinyinNumbered = ImportText.trimToNull(row.pinyinNumbered());
        if (pinyinNumbered == null) {
            // FE thường gửi kèm từ preview; nếu thiếu thì tự suy ra để tìm kiếm không dấu vẫn hoạt động
            pinyinNumbered = PinyinUtils.markedToNumbered(pinyin);
        }

        Word word = new Word();
        word.setSimplified(simplified);
        word.setTraditional(ImportText.trimToNull(row.traditional()));
        word.setPinyin(pinyin);
        word.setPinyinNumbered(pinyinNumbered);
        word.setMeaningVi(meaningVi);
        word.setMeaningEn(ImportText.trimToNull(row.meaningEn()));
        word.setPartOfSpeech(ImportText.trimToNull(row.partOfSpeech()));
        word.setHskLevel(row.hskLevel());
        word.replaceTopics(loadTopics(row.topicIds()));

        return wordRepository.save(word).getId();
    }

    /** Ghi đè các trường KHÔNG null của request lên từ đã có. */
    private Long update(ImportConfirmRow row) {
        Word word = requireExisting(row);

        String simplified = ImportText.trimToNull(row.simplified());
        String pinyin = ImportText.toMarkedPinyin(ImportText.trimToNull(row.pinyin()));
        String newSimplified = simplified != null ? simplified : word.getSimplified();
        String newPinyin = pinyin != null ? pinyin : word.getPinyin();
        if (simplified != null || pinyin != null) {
            if (wordRepository.existsBySimplifiedAndPinyinAndIdNot(newSimplified, newPinyin, word.getId())) {
                throw new ConflictException("Đã tồn tại: " + newSimplified + " [" + newPinyin + "]");
            }
            requireNoSameReading(newSimplified, newPinyin, word.getId());
        }

        if (simplified != null) {
            word.setSimplified(simplified);
        }
        String pinyinNumbered = ImportText.trimToNull(row.pinyinNumbered());
        if (pinyin != null) {
            word.setPinyin(pinyin);
            // Pinyin đổi mà không gửi bản đánh số thì tính lại để hai cột luôn đồng bộ
            word.setPinyinNumbered(pinyinNumbered != null ? pinyinNumbered : PinyinUtils.markedToNumbered(pinyin));
        } else if (pinyinNumbered != null) {
            word.setPinyinNumbered(pinyinNumbered);
        }
        String traditional = ImportText.trimToNull(row.traditional());
        if (traditional != null) {
            word.setTraditional(traditional);
        }
        String meaningVi = ImportText.trimToNull(row.meaningVi());
        if (meaningVi != null) {
            word.setMeaningVi(meaningVi);
        }
        String meaningEn = ImportText.trimToNull(row.meaningEn());
        if (meaningEn != null) {
            word.setMeaningEn(meaningEn);
        }
        String partOfSpeech = ImportText.trimToNull(row.partOfSpeech());
        if (partOfSpeech != null) {
            word.setPartOfSpeech(partOfSpeech);
        }
        if (row.hskLevel() != null) {
            word.setHskLevel(row.hskLevel());
        }
        if (row.topicIds() != null) {
            word.replaceTopics(loadTopics(row.topicIds()));
        }

        return wordRepository.save(word).getId();
    }

    /** Không đụng bảng words: chỉ xác nhận từ tồn tại rồi trả id để đánh dấu đã học. */
    private Long link(ImportConfirmRow row) {
        return requireExisting(row).getId();
    }

    /**
     * Chặn trùng theo <b>cách đọc</b> (khoá pinyin chuẩn hoá) — cùng tiêu chí bước preview dùng để báo EXISTS.
     * Khoá unique của DB chỉ so chuỗi y hệt nên {@code "xué xí"} và {@code "xuéxí"} vẫn chèn được hai dòng;
     * ở đây coi chúng là một từ để người dùng ép CREATE trên dòng EXISTS cũng không tạo bản sao.
     *
     * @param excludeId id của chính từ đang cập nhật (null khi tạo mới)
     */
    private void requireNoSameReading(String simplified, String pinyin, Long excludeId) {
        String key = ImportText.pinyinKey(pinyin);
        List<Word> sameHanzi = lookupRepository.findBySimplified(simplified);
        if (sameHanzi == null) {
            return;
        }
        for (Word w : sameHanzi) {
            if (w.getId() != null && w.getId().equals(excludeId)) {
                continue;
            }
            if (key.equals(ImportText.pinyinKey(w.getPinyin()))) {
                throw new ConflictException("Đã tồn tại: " + simplified + " [" + w.getPinyin() + "] (id " + w.getId() + ")");
            }
        }
    }

    /** Nạp từ theo {@code existingWordId}, báo lỗi rõ ràng khi thiếu hoặc không tồn tại. */
    private Word requireExisting(ImportConfirmRow row) {
        if (row.existingWordId() == null) {
            throw new BadRequestException("Thiếu existingWordId cho hành động " + row.action());
        }
        return wordRepository.findById(row.existingWordId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy từ vựng có id: " + row.existingWordId()));
    }

    /** Nạp chủ đề theo id, kiểm tra tất cả đều tồn tại; null hoặc rỗng cho tập rỗng. */
    private Set<Topic> loadTopics(List<Long> topicIds) {
        if (topicIds == null || topicIds.isEmpty()) {
            return new LinkedHashSet<>();
        }
        Set<Long> ids = new LinkedHashSet<>(topicIds);
        List<Topic> topics = topicRepository.findAllById(ids);
        if (topics.size() != ids.size()) {
            Set<Long> found = topics.stream().map(Topic::getId).collect(Collectors.toSet());
            List<Long> missing = ids.stream().filter(id -> !found.contains(id)).collect(Collectors.toList());
            throw new NotFoundException("Không tìm thấy chủ đề có id: " + missing);
        }
        return new LinkedHashSet<>(topics);
    }
}
