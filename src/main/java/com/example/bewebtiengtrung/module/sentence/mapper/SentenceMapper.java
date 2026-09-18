package com.example.bewebtiengtrung.module.sentence.mapper;

import com.example.bewebtiengtrung.module.sentence.dto.SentenceResponse;
import com.example.bewebtiengtrung.module.sentence.entity.UserSentence;
import org.springframework.stereotype.Component;

/** Chuyển đổi entity {@link UserSentence} sang DTO. */
@Component
public class SentenceMapper {

    /** Map entity sang DTO — không chạm tới proxy {@code user} nên không phát sinh truy vấn phụ. */
    public SentenceResponse toResponse(UserSentence sentence) {
        return new SentenceResponse(
                sentence.getId(),
                sentence.getHanzi(),
                sentence.getPinyin(),
                sentence.getMeaningVi(),
                sentence.getLevel() == null ? 1 : sentence.getLevel(),
                sentence.getSource(),
                sentence.getCreatedAt()
        );
    }
}
