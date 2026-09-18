package com.example.bewebtiengtrung.module.userword.mapper;

import com.example.bewebtiengtrung.module.userword.dto.UserWordResponse;
import com.example.bewebtiengtrung.module.userword.entity.UserWord;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import org.springframework.stereotype.Component;

/** Chuyển đổi bản ghi sổ từ sang DTO. */
@Component
public class UserWordMapper {

    /**
     * Gộp thông tin từ gốc và trạng thái học của người dùng.
     * Yêu cầu {@code userWord.word} đã được nạp (fetch join hoặc đang trong transaction)
     * để đọc được các trường của từ mà không sinh N+1.
     */
    public UserWordResponse toResponse(UserWord userWord) {
        Word word = userWord.getWord();
        return new UserWordResponse(
                userWord.getId(),
                word.getId(),
                word.getSimplified(),
                word.getTraditional(),
                word.getPinyin(),
                word.getMeaningVi(),
                word.getMeaningEn(),
                word.getHskLevel(),
                word.getPartOfSpeech(),
                userWord.getStatus(),
                userWord.getNote(),
                userWord.getLearnedAt()
        );
    }
}
