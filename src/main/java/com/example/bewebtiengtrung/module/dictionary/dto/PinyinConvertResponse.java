package com.example.bewebtiengtrung.module.dictionary.dto;

/** Kết quả chuyển đổi pinyin: dạng dấu, dạng số và khoá so khớp. */
public record PinyinConvertResponse(String marked, String numbered, String key) {
}
