package com.example.bewebtiengtrung.module.vocabulary.mapper;

import com.example.bewebtiengtrung.module.vocabulary.dto.CreateWordExampleRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.TopicResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordDetailResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordExampleResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordSummaryResponse;
import com.example.bewebtiengtrung.module.vocabulary.entity.Topic;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.module.vocabulary.entity.WordExample;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Chuyển đổi giữa entity {@link Word} / {@link WordExample} và các DTO tương ứng.
 */
@Component
public class WordMapper {

    private final TopicMapper topicMapper;

    public WordMapper(TopicMapper topicMapper) {
        this.topicMapper = topicMapper;
    }

    /** Bản rút gọn dùng cho danh sách và luyện tập ngẫu nhiên. */
    public WordSummaryResponse toSummary(Word word) {
        if (word == null) {
            return null;
        }
        return new WordSummaryResponse(
                word.getId(),
                word.getSimplified(),
                word.getTraditional(),
                word.getPinyin(),
                word.getMeaningVi(),
                word.getHskLevel(),
                word.getPartOfSpeech(),
                word.getAudioUrl()
        );
    }

    /**
     * Bản chi tiết kèm câu ví dụ và chủ đề.
     * Chỉ gọi khi hai tập này đã được nạp sẵn (xem WordRepository#findDetailById).
     */
    public WordDetailResponse toDetail(Word word) {
        if (word == null) {
            return null;
        }
        List<WordExampleResponse> examples = word.getExamples().stream()
                .sorted(Comparator
                        .comparing(WordExample::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(WordExample::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toExampleResponse)
                .collect(Collectors.toList());

        List<TopicResponse> topics = word.getTopics().stream()
                .sorted(Comparator
                        .comparing(Topic::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Topic::getNameVi, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(topicMapper::toResponse)
                .collect(Collectors.toList());

        return new WordDetailResponse(
                word.getId(),
                word.getSimplified(),
                word.getTraditional(),
                word.getPinyin(),
                word.getPinyinNumbered(),
                word.getMeaningVi(),
                word.getMeaningEn(),
                word.getPartOfSpeech(),
                word.getHskLevel(),
                word.getStrokeCount(),
                word.getFrequencyRank(),
                word.getAudioUrl(),
                word.getImageUrl(),
                word.getNote(),
                examples,
                topics
        );
    }

    /** Chuyển một câu ví dụ sang DTO trả về. */
    public WordExampleResponse toExampleResponse(WordExample example) {
        if (example == null) {
            return null;
        }
        return new WordExampleResponse(
                example.getId(),
                example.getSentenceZh(),
                example.getSentencePinyin(),
                example.getSentenceVi(),
                example.getAudioUrl(),
                example.getSortOrder()
        );
    }

    /**
     * Tạo entity câu ví dụ từ dữ liệu yêu cầu.
     *
     * @param defaultSortOrder thứ tự dùng khi client không gửi sortOrder
     */
    public WordExample toExampleEntity(CreateWordExampleRequest request, int defaultSortOrder) {
        WordExample example = new WordExample();
        example.setSentenceZh(request.sentenceZh());
        example.setSentencePinyin(request.sentencePinyin());
        example.setSentenceVi(request.sentenceVi());
        example.setAudioUrl(request.audioUrl());
        example.setSortOrder(request.sortOrder() != null ? request.sortOrder() : defaultSortOrder);
        return example;
    }
}
