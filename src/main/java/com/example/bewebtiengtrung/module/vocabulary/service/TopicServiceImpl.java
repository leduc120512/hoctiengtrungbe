package com.example.bewebtiengtrung.module.vocabulary.service;

import com.example.bewebtiengtrung.common.exception.ConflictException;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.vocabulary.dto.CreateTopicRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.TopicResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.UpdateTopicRequest;
import com.example.bewebtiengtrung.module.vocabulary.entity.Topic;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.module.vocabulary.mapper.TopicMapper;
import com.example.bewebtiengtrung.module.vocabulary.repository.TopicRepository;
import com.example.bewebtiengtrung.module.vocabulary.repository.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Cài đặt nghiệp vụ quản lý chủ đề từ vựng. Chỉ trả về DTO, không trả entity ra ngoài.
 */
@Service
@Transactional(readOnly = true)
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final WordRepository wordRepository;
    private final TopicMapper topicMapper;

    public TopicServiceImpl(TopicRepository topicRepository,
                            WordRepository wordRepository,
                            TopicMapper topicMapper) {
        this.topicRepository = topicRepository;
        this.wordRepository = wordRepository;
        this.topicMapper = topicMapper;
    }

    @Override
    public List<TopicResponse> findAll() {
        return topicRepository.findAllByOrderBySortOrderAscNameViAsc().stream()
                .map(topicMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TopicResponse findBySlug(String slug) {
        Topic topic = topicRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề với slug: " + slug));
        return topicMapper.toResponse(topic);
    }

    @Override
    @Transactional
    public TopicResponse create(CreateTopicRequest request) {
        // Slug là định danh công khai nên phải duy nhất
        if (topicRepository.existsBySlug(request.slug())) {
            throw new ConflictException("Slug chủ đề đã tồn tại: " + request.slug());
        }
        Topic topic = topicMapper.toEntity(request);
        return topicMapper.toResponse(topicRepository.save(topic));
    }

    @Override
    @Transactional
    public TopicResponse update(Long id, UpdateTopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề có id: " + id));

        if (topicRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new ConflictException("Slug chủ đề đã tồn tại: " + request.slug());
        }

        topic.setSlug(request.slug());
        topic.setNameVi(request.nameVi());
        topic.setNameZh(request.nameZh());
        topic.setDescription(request.description());
        topic.setIcon(request.icon());
        topic.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);

        return topicMapper.toResponse(topicRepository.save(topic));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề có id: " + id));

        // Word là phía chủ sở hữu của word_topics nên phải gỡ liên kết từ phía Word
        List<Word> linkedWords = wordRepository.findAllByTopicId(id);
        for (Word word : linkedWords) {
            word.getTopics().removeIf(t -> t.getId().equals(id));
        }
        wordRepository.saveAll(linkedWords);

        topicRepository.delete(topic);
    }
}
