package com.example.bewebtiengtrung.module.sentence.controller;

import com.example.bewebtiengtrung.module.sentence.dto.AiStatusResponse;
import com.example.bewebtiengtrung.module.sentence.service.SentenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API trạng thái AI trên máy chủ — để frontend biết có bật nút "Tạo câu mới bằng AI" hay không. */
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "AI", description = "Trạng thái cấu hình AI sinh câu (Claude)")
public class AiController {

    private final SentenceService sentenceService;

    @GetMapping("/status")
    @Operation(summary = "AI đã được cấu hình chưa, dùng model nào; kèm lý do tiếng Việt khi tắt")
    public AiStatusResponse status() {
        return sentenceService.aiStatus();
    }
}
