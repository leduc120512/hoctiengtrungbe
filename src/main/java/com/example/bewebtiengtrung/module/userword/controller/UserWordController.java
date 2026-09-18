package com.example.bewebtiengtrung.module.userword.controller;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.userword.dto.BulkUserWordRequest;
import com.example.bewebtiengtrung.module.userword.dto.UpsertUserWordRequest;
import com.example.bewebtiengtrung.module.userword.dto.UserWordResponse;
import com.example.bewebtiengtrung.module.userword.dto.UserWordStatsResponse;
import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import com.example.bewebtiengtrung.module.userword.service.UserWordService;
import com.example.bewebtiengtrung.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API sổ từ đã học của chính người dùng đang đăng nhập.
 * Controller không chứa nghiệp vụ; userId luôn lấy từ token, không nhận từ client.
 */
@RestController
@RequestMapping("/api/v1/me/words")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Sổ từ đã học", description = "Quản lý danh sách từ vựng người dùng đã học và thống kê")
public class UserWordController {

    private final UserWordService userWordService;

    @GetMapping
    @Operation(summary = "Danh sách từ đã học, lọc theo trạng thái / cấp HSK / từ khoá (có phân trang)")
    public PageResponse<UserWordResponse> list(
            @Parameter(description = "Lọc theo trạng thái: LEARNING, LEARNED, MASTERED")
            @RequestParam(name = "status", required = false) UserWordStatus status,
            @Parameter(description = "Lọc theo cấp độ HSK của từ gốc (1..9)")
            @RequestParam(name = "hskLevel", required = false) Integer hskLevel,
            @Parameter(description = "Từ khoá khớp chữ giản thể, pinyin (có/không dấu) hoặc nghĩa tiếng Việt")
            @RequestParam(name = "q", required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return userWordService.list(SecurityUtils.currentUserId(), status, hskLevel, q, pageable);
    }

    @GetMapping("/stats")
    @Operation(summary = "Thống kê sổ từ: tổng, theo trạng thái, theo cấp HSK, số từ học trong 7 ngày")
    public UserWordStatsResponse stats() {
        return userWordService.stats(SecurityUtils.currentUserId());
    }

    @GetMapping("/ids")
    @Operation(summary = "Danh sách id mọi từ có trong sổ (dùng để đánh dấu nhanh trên giao diện)")
    public List<Long> ids() {
        return userWordService.learnedWordIds(SecurityUtils.currentUserId());
    }

    @PutMapping("/{wordId}")
    @Operation(summary = "Thêm một từ vào sổ, hoặc cập nhật trạng thái / ghi chú nếu đã có")
    public UserWordResponse upsert(
            @Parameter(description = "Id từ vựng trong kho từ")
            @PathVariable("wordId") Long wordId,
            @Valid @RequestBody UpsertUserWordRequest request) {
        return userWordService.upsert(SecurityUtils.currentUserId(), wordId, request);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Đánh dấu nhiều từ cùng lúc (idempotent, chỉ tạo mới từ chưa có trong sổ)")
    public MessageResponse bulk(@Valid @RequestBody BulkUserWordRequest request) {
        int created = userWordService.bulkUpsert(
                SecurityUtils.currentUserId(), request.wordIds(), request.status());
        return new MessageResponse("Đã đánh dấu " + created + " từ");
    }

    @DeleteMapping("/{wordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Gỡ một từ khỏi sổ từ đã học")
    public void remove(
            @Parameter(description = "Id từ vựng trong kho từ")
            @PathVariable("wordId") Long wordId) {
        userWordService.remove(SecurityUtils.currentUserId(), wordId);
    }
}
