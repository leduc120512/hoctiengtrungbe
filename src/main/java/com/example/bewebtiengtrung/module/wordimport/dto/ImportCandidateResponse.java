package com.example.bewebtiengtrung.module.wordimport.dto;

/**
 * Gợi ý chữ Hán khi người dùng chỉ nhập pinyin (tra ngược từ CC-CEDICT).
 *
 * @param inSystem chữ giản thể này đã có trong bảng words của hệ thống hay chưa
 */
public record ImportCandidateResponse(
        String simplified,
        String traditional,
        String pinyinMarked,
        String meaningEn,
        boolean inSystem
) {
}
