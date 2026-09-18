package com.example.bewebtiengtrung.module.wordimport.dto;

import java.util.List;

/**
 * Kết quả duyệt trước toàn bộ lô: thống kê + từng dòng theo đúng thứ tự gửi lên.
 */
public record ImportPreviewResponse(
        ImportPreviewSummary summary,
        List<ImportPreviewRow> rows
) {
}
