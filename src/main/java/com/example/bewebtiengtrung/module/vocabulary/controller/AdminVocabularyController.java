package com.example.bewebtiengtrung.module.vocabulary.controller;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.CreateTopicRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.CreateWordExampleRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.CreateWordRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.TopicResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.UpdateTopicRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.UpdateWordRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordDetailResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.WordExampleResponse;
import com.example.bewebtiengtrung.module.vocabulary.service.TopicService;
import com.example.bewebtiengtrung.module.vocabulary.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API quản trị từ vựng và chủ đề. Toàn bộ endpoint yêu cầu quyền ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Quản trị từ vựng", description = "API dành cho quản trị viên để quản lý từ vựng và chủ đề")
public class AdminVocabularyController {

    private final WordService wordService;
    private final TopicService topicService;

    public AdminVocabularyController(WordService wordService, TopicService topicService) {
        this.wordService = wordService;
        this.topicService = topicService;
    }

    @Operation(summary = "Tạo mới một từ vựng")
    @PostMapping("/words")
    @ResponseStatus(HttpStatus.CREATED)
    public WordDetailResponse createWord(@Valid @RequestBody CreateWordRequest request) {
        return wordService.create(request);
    }

    @Operation(summary = "Cập nhật toàn phần một từ vựng")
    @PutMapping("/words/{id}")
    public WordDetailResponse updateWord(@PathVariable("id") Long id,
                                         @Valid @RequestBody UpdateWordRequest request) {
        return wordService.update(id, request);
    }

    @Operation(summary = "Xoá một từ vựng cùng toàn bộ câu ví dụ của nó")
    @DeleteMapping("/words/{id}")
    public MessageResponse deleteWord(@PathVariable("id") Long id) {
        wordService.delete(id);
        return new MessageResponse("Đã xoá từ vựng có id: " + id);
    }

    @Operation(summary = "Thêm một câu ví dụ cho từ vựng")
    @PostMapping("/words/{id}/examples")
    @ResponseStatus(HttpStatus.CREATED)
    public WordExampleResponse addWordExample(@PathVariable("id") Long id,
                                              @Valid @RequestBody CreateWordExampleRequest request) {
        return wordService.addExample(id, request);
    }

    @Operation(summary = "Tạo mới một chủ đề từ vựng")
    @PostMapping("/topics")
    @ResponseStatus(HttpStatus.CREATED)
    public TopicResponse createTopic(@Valid @RequestBody CreateTopicRequest request) {
        return topicService.create(request);
    }

    @Operation(summary = "Cập nhật toàn phần một chủ đề từ vựng")
    @PutMapping("/topics/{id}")
    public TopicResponse updateTopic(@PathVariable("id") Long id,
                                     @Valid @RequestBody UpdateTopicRequest request) {
        return topicService.update(id, request);
    }

    @Operation(summary = "Xoá một chủ đề và gỡ liên kết của nó khỏi mọi từ vựng")
    @DeleteMapping("/topics/{id}")
    public MessageResponse deleteTopic(@PathVariable("id") Long id) {
        topicService.delete(id);
        return new MessageResponse("Đã xoá chủ đề có id: " + id);
    }
}
