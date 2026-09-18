package com.example.bewebtiengtrung.module.wordimport.service;

import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.module.dictionary.service.CedictEntry;
import com.example.bewebtiengtrung.module.dictionary.service.CedictService;
import com.example.bewebtiengtrung.module.dictionary.service.PinyinUtils;
import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import com.example.bewebtiengtrung.module.userword.service.UserWordService;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.module.wordimport.dto.DictionarySenseResponse;
import com.example.bewebtiengtrung.module.wordimport.dto.ExistingWordBrief;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportAction;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportCandidateResponse;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmRequest;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmResponse;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmRow;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewRequest;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewResponse;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewRow;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewSummary;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportRowError;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportRowInput;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportRowStatus;
import com.example.bewebtiengtrung.module.wordimport.repository.ImportWordLookupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Cài đặt nghiệp vụ nhập từ vựng có duyệt.
 *
 * <p>{@code preview} chạy thuần đọc theo đúng 10 bước của contract (chuẩn hoá, tra CC-CEDICT,
 * so pinyin, điền dữ liệu, đối chiếu hệ thống, trùng trong lô, thiếu nghĩa, thống kê).
 * {@code confirm} không mở transaction bao ngoài: từng dòng được {@link ImportRowExecutor} chạy trong
 * transaction riêng để một dòng lỗi không kéo theo cả lô.</p>
 */
@Service
@Transactional(readOnly = true)
public class ImportServiceImpl implements ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);

    /** Số gợi ý chữ Hán tối đa khi người dùng chỉ nhập pinyin. */
    private static final int CANDIDATE_LIMIT = 8;

    private final CedictService cedictService;
    private final UserWordService userWordService;
    private final ImportWordLookupRepository lookupRepository;
    private final ImportRowExecutor rowExecutor;

    public ImportServiceImpl(CedictService cedictService,
                             UserWordService userWordService,
                             ImportWordLookupRepository lookupRepository,
                             ImportRowExecutor rowExecutor) {
        this.cedictService = cedictService;
        this.userWordService = userWordService;
        this.lookupRepository = lookupRepository;
        this.rowExecutor = rowExecutor;
    }

    // ------------------------------------------------------------------
    // Preview
    // ------------------------------------------------------------------

    @Override
    public ImportPreviewResponse preview(Long userId, ImportPreviewRequest request) {
        // Nạp một lần cho cả lô: tập word_id người dùng đã học (tính cờ alreadyLearned)
        List<Long> learnedList = userWordService.learnedWordIds(userId);
        Set<Long> learnedIds = learnedList == null ? Set.of() : new HashSet<>(learnedList);

        // Khoá "giản thể|pinyinKey" của các dòng đã duyệt, để phát hiện trùng ngay trong lô
        Map<String, Integer> seenKeys = new HashMap<>();

        List<ImportPreviewRow> rows = new ArrayList<>(request.rows().size());
        for (int i = 0; i < request.rows().size(); i++) {
            rows.add(previewRow(i, request.rows().get(i), request.defaultHskLevel(), learnedIds, seenKeys));
        }

        int willCreate = 0;
        int existing = 0;
        int needsAttention = 0;
        int errors = 0;
        for (ImportPreviewRow row : rows) {
            if (row.suggestedAction() == ImportAction.CREATE) {
                willCreate++;
            }
            switch (row.status()) {
                case EXISTS -> existing++;
                case WARNING -> needsAttention++;
                case ERROR -> errors++;
                default -> {
                    // OK: không cần đếm riêng
                }
            }
        }
        ImportPreviewSummary summary = new ImportPreviewSummary(rows.size(), willCreate, existing, needsAttention, errors);
        return new ImportPreviewResponse(summary, rows);
    }

    /** Duyệt MỘT dòng theo đúng thứ tự các bước trong contract. */
    private ImportPreviewRow previewRow(int index, ImportRowInput raw, Integer defaultHskLevel,
                                        Set<Long> learnedIds, Map<String, Integer> seenKeys) {
        // Bước 1: chuẩn hoá — NFC + trim mọi trường, chuỗi rỗng coi như null (phần tử null coi như dòng trống)
        String simplified = raw == null ? null : ImportText.trimToNull(raw.simplified());
        String userPinyin = raw == null ? null : ImportText.trimToNull(raw.pinyin());
        String userMeaningVi = raw == null ? null : ImportText.trimToNull(raw.meaningVi());
        String userMeaningEn = raw == null ? null : ImportText.trimToNull(raw.meaningEn());
        ImportRowInput input = new ImportRowInput(simplified, userPinyin, userMeaningVi, userMeaningEn);

        // Pinyin người dùng đưa về dạng có dấu NGAY từ đầu để mọi bước so khớp / lưu trữ dùng chung một dạng
        // (gõ "xue2 xi2" hay "Xue2Xi2" đều thành "xué xí"; cột words.pinyin luôn là dạng có dấu).
        String userPinyinMarked = ImportText.toMarkedPinyin(userPinyin);

        Draft d = new Draft(index, input);
        d.meaningVi = userMeaningVi;
        d.hskLevel = defaultHskLevel;

        // Bước 2: thiếu chữ Hán (hoặc không phải chữ Hán) thì gợi ý theo pinyin rồi dừng
        if (simplified == null || !PinyinUtils.isChinese(simplified)) {
            if (userPinyin != null) {
                // CC-CEDICT có thể có nhiều mục cùng chữ giản thể + cùng âm (khác nghĩa) — gộp lại
                // để người dùng không thấy một chữ hiện hai lần trong danh sách gợi ý.
                java.util.Set<String> seenCandidates = new java.util.LinkedHashSet<>();
                d.candidates = nullSafe(cedictService.reverseLookup(userPinyinMarked, CANDIDATE_LIMIT * 2)).stream()
                        .filter(e -> seenCandidates.add(e.simplified()))
                        .limit(CANDIDATE_LIMIT)
                        .map(this::toCandidate)
                        .collect(Collectors.toList());
                d.pinyin = userPinyinMarked;
                d.raise(ImportRowStatus.WARNING);
                d.suggestedAction = ImportAction.NEEDS_INPUT;
                d.messages.add("Thiếu chữ Hán — chọn một trong các gợi ý");
            } else {
                d.raise(ImportRowStatus.ERROR);
                d.suggestedAction = ImportAction.SKIP;
                d.messages.add("Thiếu cả chữ Hán lẫn pinyin");
            }
            return d.toRow();
        }
        d.simplified = simplified;

        // Bước 3: tra từ điển theo chữ giản thể
        List<CedictEntry> senses = nullSafe(cedictService.lookupSimplified(simplified));
        d.dictionaryFound = !senses.isEmpty();
        d.dictionarySenses = senses.stream().map(this::toSense).collect(Collectors.toList());
        if (!d.dictionaryFound) {
            d.raise(ImportRowStatus.WARNING);
            d.messages.add("Chữ Hán không có trong từ điển CC-CEDICT — kiểm tra lại");
        }

        // Bước 4: chọn mục từ điển khớp pinyin người dùng (nếu có); không có pinyin thì lấy mục đầu
        CedictEntry first = senses.isEmpty() ? null : senses.get(0);
        CedictEntry sense = first;
        if (userPinyin != null && !senses.isEmpty()) {
            String userKey = ImportText.pinyinKey(userPinyinMarked);
            CedictEntry matched = senses.stream()
                    .filter(e -> userKey.equals(ImportText.pinyinKey(e.pinyinNumbered())))
                    .findFirst()
                    .orElse(null);
            d.pinyinMatchesDictionary = matched != null;
            if (matched != null) {
                sense = matched;
            } else {
                d.raise(ImportRowStatus.WARNING);
                d.messages.add("Pinyin '" + userPinyin + "' khác từ điển '" + first.pinyinMarked()
                        + "' — kiểm tra thanh điệu");
            }
        }

        // Bước 5: điền dữ liệu
        if (sense != null) {
            d.traditional = sense.traditional();
            d.dictionaryPinyin = first.pinyinMarked();
        }
        if (userPinyin != null) {
            d.pinyin = userPinyinMarked;
        } else if (sense != null) {
            d.pinyin = PinyinUtils.numberedToMarked(sense.pinyinNumbered(), false);
        }
        if (userMeaningEn != null) {
            d.meaningEn = userMeaningEn;
        } else if (sense != null) {
            d.meaningEn = cedictService.suggestEnglish(sense);
        }

        // Bước 6 (phần tra cứu): mọi bản ghi cùng chữ giản thể trong bảng words
        List<Word> sameHanzi = nullSafe(lookupRepository.findBySimplified(simplified));

        if (d.pinyin == null) {
            // Không có pinyin từ người dùng lẫn từ điển. Nếu hệ thống có ĐÚNG MỘT bản ghi cùng chữ thì lấy pinyin
            // của nó (bước 6 sẽ nhận ra là EXISTS/LINK); ngược lại không thể tạo từ vì words.pinyin NOT NULL.
            if (sameHanzi.size() == 1) {
                d.pinyin = sameHanzi.get(0).getPinyin();
            } else {
                d.raise(ImportRowStatus.ERROR);
                d.suggestedAction = ImportAction.SKIP;
                if (sameHanzi.isEmpty()) {
                    d.messages.add("Thiếu pinyin và không tra được trong từ điển CC-CEDICT");
                } else {
                    d.messages.add("Thiếu pinyin — hệ thống có nhiều cách đọc cho " + simplified + ": "
                            + sameHanzi.stream().map(Word::getPinyin).collect(Collectors.joining(", ")));
                }
                return d.toRow();
            }
        }
        d.pinyinNumbered = PinyinUtils.markedToNumbered(d.pinyin);
        String pinyinKey = ImportText.pinyinKey(d.pinyin);

        // Bước 6: đối chiếu với bảng words của hệ thống
        Word existing = null;
        List<String> otherPinyins = new ArrayList<>();
        for (Word w : sameHanzi) {
            if (pinyinKey.equals(ImportText.pinyinKey(w.getPinyin()))) {
                if (existing == null) {
                    existing = w;
                }
            } else {
                otherPinyins.add(w.getPinyin());
            }
        }
        if (existing != null) {
            d.existingWord = new ExistingWordBrief(existing.getId(), existing.getSimplified(), existing.getPinyin(),
                    existing.getMeaningVi(), existing.getMeaningEn(), existing.getHskLevel(),
                    learnedIds.contains(existing.getId()));
            d.raise(ImportRowStatus.EXISTS);
            d.suggestedAction = ImportAction.LINK;
            d.messages.add("Đã có trong hệ thống (id " + existing.getId() + ")");
        }
        if (!otherPinyins.isEmpty()) {
            d.messages.add("Hệ thống đã có " + simplified + " với pinyin " + String.join(", ", otherPinyins)
                    + " (từ đa âm?)");
        }

        // Bước 7: trùng ngay trong lô (cùng chữ Hán + cùng pinyin đã chuẩn hoá với dòng trước)
        String batchKey = simplified + "|" + pinyinKey;
        Integer firstIndex = seenKeys.putIfAbsent(batchKey, index);
        if (firstIndex != null) {
            d.raise(ImportRowStatus.ERROR);
            d.suggestedAction = ImportAction.SKIP;
            // Hiển thị cho người đọc nên đánh số từ 1 (index trong JSON vẫn bắt đầu từ 0)
            d.messages.add("Trùng với dòng " + (firstIndex + 1));
        }

        // Bước 8: tạo mới bắt buộc có nghĩa tiếng Việt
        if (userMeaningVi == null && d.status != ImportRowStatus.EXISTS) {
            d.raise(ImportRowStatus.ERROR);
            d.suggestedAction = ImportAction.SKIP;
            d.messages.add("Thiếu nghĩa tiếng Việt");
        }

        // Bước 9: mặc định còn lại
        if (d.suggestedAction == null) {
            d.suggestedAction = ImportAction.CREATE;
        }
        return d.toRow();
    }

    private ImportCandidateResponse toCandidate(CedictEntry entry) {
        return new ImportCandidateResponse(entry.simplified(), entry.traditional(), entry.pinyinMarked(),
                cedictService.suggestEnglish(entry), lookupRepository.existsBySimplified(entry.simplified()));
    }

    private DictionarySenseResponse toSense(CedictEntry entry) {
        return new DictionarySenseResponse(entry.traditional(), entry.simplified(), entry.pinyinNumbered(),
                entry.pinyinMarked(), entry.definitions());
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list == null ? List.of() : list;
    }

    // ------------------------------------------------------------------
    // Confirm
    // ------------------------------------------------------------------

    /**
     * Không mở transaction ở đây (NOT_SUPPORTED): mỗi dòng đã có transaction riêng trong
     * {@link ImportRowExecutor}, còn {@code bulkUpsert} tự quản transaction của nó. Nếu để transaction
     * readOnly của lớp bao ngoài thì bước đánh dấu đã học sẽ bị từ chối ghi.
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ImportConfirmResponse confirm(Long userId, ImportConfirmRequest request) {
        int created = 0;
        int updated = 0;
        int linked = 0;
        int skipped = 0;
        List<Long> wordIds = new ArrayList<>();
        List<ImportRowError> errors = new ArrayList<>();

        List<ImportConfirmRow> rows = request.rows();
        for (int i = 0; i < rows.size(); i++) {
            ImportConfirmRow row = rows.get(i);
            if (row == null) {
                errors.add(new ImportRowError(i, "Dòng trống"));
                continue;
            }
            try {
                Long wordId = rowExecutor.execute(row);
                if (wordId == null) {
                    skipped++;
                    continue;
                }
                wordIds.add(wordId);
                switch (row.action()) {
                    case CREATE -> created++;
                    case UPDATE -> updated++;
                    case LINK -> linked++;
                    default -> {
                        // SKIP / NEEDS_INPUT không tới được đây vì executor trả null
                    }
                }
            } catch (ApiException ex) {
                errors.add(new ImportRowError(i, ex.getMessage()));
            } catch (DataIntegrityViolationException ex) {
                errors.add(new ImportRowError(i, "Vi phạm ràng buộc dữ liệu (có thể trùng chữ Hán + pinyin)"));
            } catch (RuntimeException ex) {
                log.warn("Lỗi không mong đợi khi nhập dòng {}: {}", i, ex.getMessage(), ex);
                errors.add(new ImportRowError(i, "Lỗi không xác định: " + ex.getMessage()));
            }
        }

        int markedLearned = 0;
        if (request.markAsLearned() && !wordIds.isEmpty()) {
            List<Long> distinctIds = wordIds.stream().distinct().collect(Collectors.toList());
            try {
                userWordService.bulkUpsert(userId, distinctIds, UserWordStatus.LEARNED);
                markedLearned = distinctIds.size();
            } catch (RuntimeException ex) {
                // Từ đã được ghi xong; chỉ bước đánh dấu thất bại thì báo riêng, không làm hỏng kết quả
                log.warn("Không đánh dấu đã học được cho user {}: {}", userId, ex.getMessage(), ex);
                errors.add(new ImportRowError(-1, "Đã nhập từ nhưng không đánh dấu đã học được: " + ex.getMessage()));
            }
        }

        return new ImportConfirmResponse(created, updated, linked, skipped, markedLearned, wordIds, errors);
    }

    // ------------------------------------------------------------------
    // Bản nháp của một dòng preview
    // ------------------------------------------------------------------

    /**
     * Bộ chứa tạm trong lúc duyệt một dòng. Trạng thái chỉ được nâng lên, không hạ xuống,
     * theo thứ tự OK &lt; WARNING &lt; EXISTS &lt; ERROR (EXISTS thắng WARNING vì bản ghi đã có
     * trong hệ thống là căn cứ đáng tin hơn cảnh báo từ điển; mọi cảnh báo vẫn nằm trong messages).
     */
    private static final class Draft {
        final int index;
        final ImportRowInput input;
        String simplified;
        String traditional;
        String pinyin;
        String pinyinNumbered;
        String meaningVi;
        String meaningEn;
        Integer hskLevel;
        boolean dictionaryFound;
        Boolean pinyinMatchesDictionary;
        String dictionaryPinyin;
        List<DictionarySenseResponse> dictionarySenses = List.of();
        List<ImportCandidateResponse> candidates = List.of();
        ExistingWordBrief existingWord;
        ImportRowStatus status = ImportRowStatus.OK;
        ImportAction suggestedAction;
        final List<String> messages = new ArrayList<>();

        Draft(int index, ImportRowInput input) {
            this.index = index;
            this.input = input;
        }

        void raise(ImportRowStatus candidate) {
            if (rank(candidate) > rank(status)) {
                status = candidate;
            }
        }

        private static int rank(ImportRowStatus s) {
            return switch (s) {
                case OK -> 0;
                case WARNING -> 1;
                case EXISTS -> 2;
                case ERROR -> 3;
            };
        }

        ImportPreviewRow toRow() {
            return new ImportPreviewRow(index, input, simplified, traditional, pinyin, pinyinNumbered,
                    meaningVi, meaningEn, hskLevel, dictionaryFound, pinyinMatchesDictionary, dictionaryPinyin,
                    dictionarySenses, candidates, existingWord, status,
                    suggestedAction == null ? ImportAction.CREATE : suggestedAction, List.copyOf(messages));
        }
    }
}
