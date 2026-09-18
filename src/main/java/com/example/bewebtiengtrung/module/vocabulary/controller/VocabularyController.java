package com.example.bewebtiengtrung.module.vocabulary.controller;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.TopicResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordDetailResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordSummaryResponse;
import com.example.bewebtiengtrung.module.vocabulary.service.TopicService;
import com.example.bewebtiengtrung.module.vocabulary.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API công khai để tra cứu chủ đề và từ vựng.
 * Toàn bộ endpoint ở đây đều là GET và không yêu cầu đăng nhập.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Từ vựng", description = "API công khai tra cứu chủ đề và từ vựng tiếng Trung")
public class VocabularyController {

    private final WordService wordService;
    private final TopicService topicService;

    public VocabularyController(WordService wordService, TopicService topicService) {
        this.wordService = wordService;
        this.topicService = topicService;
    }

    @Operation(summary = "Lấy danh sách toàn bộ chủ đề từ vựng, sắp xếp theo thứ tự hiển thị")
    @GetMapping("/topics")
    public List<TopicResponse> listTopics() {
        return topicService.findAll();
    }

    @Operation(summary = "Lấy chi tiết một chủ đề theo slug")
    @GetMapping("/topics/{slug}")
    public TopicResponse getTopic(
            @Parameter(description = "Slug của chủ đề, ví dụ: gia-dinh")
            @PathVariable("slug") String slug) {
        return topicService.findBySlug(slug);
    }

    @Operation(summary = "Tìm kiếm từ vựng theo từ khoá, cấp độ HSK và chủ đề (có phân trang)")
    @GetMapping("/words")
    public PageResponse<WordSummaryResponse> searchWords(
            @Parameter(description = "Từ khoá khớp với chữ giản thể, pinyin hoặc nghĩa tiếng Việt")
            @RequestParam(name = "q", required = false) String q,
            @Parameter(description = "Lọc theo cấp độ HSK (1..9)")
            @RequestParam(name = "hskLevel", required = false) Integer hskLevel,
            @Parameter(description = "Lọc theo id chủ đề")
            @RequestParam(name = "topicId", required = false) Long topicId,
            @PageableDefault(size = 20) Pageable pageable) {
        return wordService.search(q, hskLevel, topicId, pageable);
    }

    @Operation(summary = "Lấy ngẫu nhiên một số từ vựng để luyện tập")
    @GetMapping("/words/random")
    public List<WordSummaryResponse> randomWords(
            @Parameter(description = "Số từ cần lấy, tối đa 100")
            @RequestParam(name = "count", defaultValue = "10") int count,
            @Parameter(description = "Chỉ lấy từ thuộc cấp độ HSK này")
            @RequestParam(name = "hskLevel", required = false) Integer hskLevel) {
        return wordService.findRandom(count, hskLevel);
    }

    @Operation(summary = "Lấy chi tiết một từ vựng kèm câu ví dụ và chủ đề")
    @GetMapping("/words/{id}")
    public WordDetailResponse getWord(
            @Parameter(description = "Id của từ vựng")
            @PathVariable("id") Long id) {
        return wordService.findById(id);
    }
}
