package com.example.bewebtiengtrung.module.wordimport.dto;

import java.util.List;

/**
 * Một mục nghĩa trong CC-CEDICT ứng với chữ Hán người dùng nhập (từ đa âm cho nhiều mục).
 */
public record DictionarySenseResponse(
        String traditional,
        String simplified,
        String pinyinNumbered,
        String pinyinMarked,
        List<String> definitions
) {
}
