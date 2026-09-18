package com.example.bewebtiengtrung.module.vocabulary.mapper;

import com.example.bewebtiengtrung.module.vocabulary.dto.CreateTopicRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.TopicResponse;
import com.example.bewebtiengtrung.module.vocabulary.entity.Topic;
import org.springframework.stereotype.Component;

/**
 * Chuyển đổi giữa entity {@link Topic} và các DTO tương ứng.
 */
@Component
public class TopicMapper {

    /** Chuyển entity chủ đề sang DTO trả về cho client. */
    public TopicResponse toResponse(Topic topic) {
        if (topic == null) {
            return null;
        }
        return new TopicResponse(
                topic.getId(),
                topic.getSlug(),
                topic.getNameVi(),
                topic.getNameZh(),
                topic.getDescription(),
                topic.getIcon(),
                topic.getSortOrder()
        );
    }

    /** Tạo entity chủ đề mới từ dữ liệu yêu cầu. */
    public Topic toEntity(CreateTopicRequest request) {
        Topic topic = new Topic();
        topic.setSlug(request.slug());
        topic.setNameVi(request.nameVi());
        topic.setNameZh(request.nameZh());
        topic.setDescription(request.description());
        topic.setIcon(request.icon());
        topic.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        return topic;
    }
}
