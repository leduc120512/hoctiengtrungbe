package com.example.bewebtiengtrung.module.wordimport.dto;

/**
 * Thống kê nhanh của lô duyệt trước.
 *
 * @param willCreate     số dòng đề xuất CREATE
 * @param existing       số dòng trạng thái EXISTS
 * @param needsAttention số dòng trạng thái WARNING
 * @param errors         số dòng trạng thái ERROR
 */
public record ImportPreviewSummary(
        int total,
        int willCreate,
        int existing,
        int needsAttention,
        int errors
) {
}
