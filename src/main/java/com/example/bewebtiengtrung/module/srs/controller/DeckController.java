package com.example.bewebtiengtrung.module.srs.controller;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.srs.dto.DeckRequest;
import com.example.bewebtiengtrung.module.srs.dto.DeckResponse;
import com.example.bewebtiengtrung.module.srs.dto.FlashcardRequest;
import com.example.bewebtiengtrung.module.srs.dto.FlashcardResponse;
import com.example.bewebtiengtrung.module.srs.service.DeckService;
import com.example.bewebtiengtrung.module.srs.service.SrsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API quản lý bộ thẻ và thẻ ghi nhớ.
 * Quyền sở hữu được kiểm tra ở tầng service, controller không chứa nghiệp vụ.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Flashcard - Bộ thẻ", description = "Quản lý bộ thẻ và thẻ ghi nhớ")
public class DeckController {

    private final DeckService deckService;
    private final SrsService srsService;

    @GetMapping("/decks")
    @Operation(summary = "Danh sách bộ thẻ công khai, bộ thẻ hệ thống và bộ thẻ của tôi")
    public PageResponse<DeckResponse> list(
            @Parameter(description = "Lọc theo cấp độ HSK") @RequestParam(required = false) Integer hskLevel,
            @Parameter(description = "Lọc theo chủ đề") @RequestParam(required = false) Long topicId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return deckService.list(hskLevel, topicId, pageable);
    }

    @GetMapping("/decks/{id}")
    @Operation(summary = "Xem chi tiết một bộ thẻ")
    public DeckResponse get(@PathVariable Long id) {
        return deckService.get(id);
    }

    @PostMapping("/decks")
    @Operation(summary = "Tạo bộ thẻ mới của tôi")
    public ResponseEntity<DeckResponse> create(@Valid @RequestBody DeckRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deckService.create(request));
    }

    @PutMapping("/decks/{id}")
    @Operation(summary = "Cập nhật bộ thẻ (chỉ chủ sở hữu hoặc quản trị viên)")
    public DeckResponse update(@PathVariable Long id, @Valid @RequestBody DeckRequest request) {
        return deckService.update(id, request);
    }

    @DeleteMapping("/decks/{id}")
    @Operation(summary = "Xoá bộ thẻ (chỉ chủ sở hữu hoặc quản trị viên)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deckService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/decks/{id}/cards")
    @Operation(summary = "Danh sách thẻ trong bộ thẻ")
    public PageResponse<FlashcardResponse> listCards(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "sortOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        return deckService.listCards(id, pageable);
    }

    @PostMapping("/decks/{id}/cards")
    @Operation(summary = "Thêm thẻ vào bộ thẻ")
    public ResponseEntity<FlashcardResponse> addCard(@PathVariable Long id,
                                                     @Valid @RequestBody FlashcardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deckService.addCard(id, request));
    }

    @PutMapping("/cards/{id}")
    @Operation(summary = "Cập nhật một thẻ ghi nhớ")
    public FlashcardResponse updateCard(@PathVariable Long id, @Valid @RequestBody FlashcardRequest request) {
        return deckService.updateCard(id, request);
    }

    @DeleteMapping("/cards/{id}")
    @Operation(summary = "Xoá một thẻ ghi nhớ")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        deckService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/decks/{id}/subscribe")
    @Operation(summary = "Đăng ký học bộ thẻ - tạo trạng thái ôn tập cho các thẻ chưa có")
    public MessageResponse subscribe(@PathVariable Long id) {
        return srsService.subscribe(id);
    }
}
