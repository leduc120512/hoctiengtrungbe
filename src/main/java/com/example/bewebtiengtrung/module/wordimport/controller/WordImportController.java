package com.example.bewebtiengtrung.module.wordimport.controller;

import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmRequest;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmResponse;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewRequest;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewResponse;
import com.example.bewebtiengtrung.module.wordimport.service.ImportService;
import com.example.bewebtiengtrung.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API nhập từ vựng có duyệt (dành cho quản trị viên).
 *
 * <p>Luồng: dán danh sách thô, gọi {@code /preview} để hệ thống tra từ điển và đối chiếu,
 * người dùng sửa/chọn hành động từng dòng rồi gọi {@code /confirm} để ghi vào MySQL
 * và (tuỳ chọn) đánh dấu đã học.</p>
 */
@RestController
@RequestMapping("/api/v1/admin/words/import")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Nhập từ vựng", description = "Nhập từ vựng hàng loạt có tra từ điển CC-CEDICT và duyệt trước khi lưu")
public class WordImportController {

    private final ImportService importService;

    public WordImportController(ImportService importService) {
        this.importService = importService;
    }

    @Operation(summary = "Duyệt trước một lô từ vựng: tra từ điển, đối chiếu hệ thống, phát hiện trùng")
    @PostMapping("/preview")
    public ImportPreviewResponse preview(@Valid @RequestBody ImportPreviewRequest request) {
        return importService.preview(SecurityUtils.currentUserId(), request);
    }

    @Operation(summary = "Thực thi nhập các dòng đã duyệt và (tuỳ chọn) đánh dấu đã học")
    @PostMapping("/confirm")
    public ImportConfirmResponse confirm(@Valid @RequestBody ImportConfirmRequest request) {
        return importService.confirm(SecurityUtils.currentUserId(), request);
    }
}
