package com.example.bewebtiengtrung.module.vocabulary.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.CreateWordExampleRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.CreateWordRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.UpdateWordRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordDetailResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordExampleResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordSummaryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Nghiệp vụ quản lý từ vựng.
 */
public interface WordService {

    /** Tìm kiếm từ vựng theo từ khoá / cấp độ HSK / chủ đề, có phân trang. */
    PageResponse<WordSummaryResponse> search(String q, Integer hskLevel, Long topicId, Pageable pageable);

    /** Lấy chi tiết một từ kèm câu ví dụ và chủ đề. */
    WordDetailResponse findById(Long id);

    /** Lấy ngẫu nhiên {@code count} từ để luyện tập, lọc theo cấp độ HSK nếu có. */
    List<WordSummaryResponse> findRandom(int count, Integer hskLevel);

    /** Tạo từ mới; ném ConflictException nếu cặp (giản thể, pinyin) đã tồn tại. */
    WordDetailResponse create(CreateWordRequest request);

    /** Cập nhật toàn phần một từ. */
    WordDetailResponse update(Long id, UpdateWordRequest request);

    /** Xoá một từ cùng toàn bộ câu ví dụ và liên kết chủ đề của nó. */
    void delete(Long id);

    /** Thêm một câu ví dụ cho từ. */
    WordExampleResponse addExample(Long wordId, CreateWordExampleRequest request);
}
