package com.example.bewebtiengtrung.module.srs.controller;

import com.example.bewebtiengtrung.module.srs.dto.ReviewCardResponse;
import com.example.bewebtiengtrung.module.srs.dto.ReviewRequest;
import com.example.bewebtiengtrung.module.srs.dto.ReviewStateResponse;
import com.example.bewebtiengtrung.module.srs.dto.SrsStatsResponse;
import com.example.bewebtiengtrung.module.srs.service.SrsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** API ôn tập giãn cách (SM-2) của chính người dùng đang đăng nhập. */
@RestController
@RequestMapping("/api/v1/me/srs")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "SRS - Ôn tập giãn cách", description = "Lịch ôn tập, chấm điểm và thống kê theo thuật toán SM-2")
public class MeSrsController {

    private final SrsService srsService;

    @GetMapping("/due")
    @Operation(summary = "Danh sách thẻ đến hạn ôn tập, sắp xếp theo hạn tăng dần")
    public List<ReviewCardResponse> due(
            @Parameter(description = "Chỉ lấy thẻ thuộc bộ thẻ này") @RequestParam(required = false) Long deckId,
            @Parameter(description = "Số thẻ tối đa (mặc định 20, tối đa 200)")
            @RequestParam(defaultValue = "20") int limit) {
        return srsService.getDue(deckId, limit);
    }

    @PostMapping("/review")
    @Operation(summary = "Chấm điểm một lần ôn thẻ và cập nhật lịch ôn theo SM-2")
    public ReviewStateResponse review(@Valid @RequestBody ReviewRequest request) {
        return srsService.review(request);
    }

    @GetMapping("/stats")
    @Operation(summary = "Thống kê tiến độ ôn tập của tôi")
    public SrsStatsResponse stats() {
        return srsService.stats();
    }
}
