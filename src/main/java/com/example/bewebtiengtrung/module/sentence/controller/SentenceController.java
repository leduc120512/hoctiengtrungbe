package com.example.bewebtiengtrung.module.sentence.controller;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.module.sentence.dto.BulkSentenceRequest;
import com.example.bewebtiengtrung.module.sentence.dto.BulkSentenceResponse;
import com.example.bewebtiengtrung.module.sentence.dto.GenerateSentencesRequest;
import com.example.bewebtiengtrung.module.sentence.dto.GenerateSentencesResponse;
import com.example.bewebtiengtrung.module.sentence.dto.SentenceResponse;
import com.example.bewebtiengtrung.module.sentence.entity.SentenceSource;
import com.example.bewebtiengtrung.module.sentence.service.SentenceService;
import com.example.bewebtiengtrung.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** API kho "Câu của tôi" của người dùng đang đăng nhập. */
@RestController
@RequestMapping("/api/v1/me/sentences")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Câu của tôi", description = "Câu luyện nghe ghép từ những từ đã học; thêm tay hoặc nhờ AI sinh")
public class SentenceController {

    private final SentenceService sentenceService;

    @GetMapping
    @Operation(summary = "Toàn bộ câu của tôi, sắp theo cấp rồi theo thứ tự thêm vào")
    public List<SentenceResponse> listAll() {
        return sentenceService.listAll(SecurityUtils.currentUserId());
    }

    @PostMapping("/bulk")
    @Operation(summary = "Thêm nhiều câu tự dán (nguồn MANUAL); câu trùng bị bỏ qua")
    public BulkSentenceResponse addBulk(@Valid @RequestBody BulkSentenceRequest request) {
        return sentenceService.addBulk(SecurityUtils.currentUserId(), request, SentenceSource.MANUAL);
    }

    @PostMapping("/generate")
    @Operation(summary = "Nhờ AI sinh câu mới chỉ dùng chữ trong vốn từ đã học (503 nếu chưa cấu hình AI)")
    public GenerateSentencesResponse generate(@Valid @RequestBody GenerateSentencesRequest request) {
        return sentenceService.generate(SecurityUtils.currentUserId(), request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá một câu của tôi")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sentenceService.delete(SecurityUtils.currentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Xoá mọi câu theo nguồn (MANUAL hoặc AI; không cho xoá BUILTIN)")
    public MessageResponse deleteBySource(
            @Parameter(description = "Nguồn câu cần xoá: MANUAL hoặc AI", required = true)
            @RequestParam SentenceSource source) {
        int deleted = sentenceService.deleteBySource(SecurityUtils.currentUserId(), source);
        return new MessageResponse("Đã xoá " + deleted + " câu");
    }
}
