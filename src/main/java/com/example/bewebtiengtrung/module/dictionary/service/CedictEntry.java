package com.example.bewebtiengtrung.module.dictionary.service;

import java.util.List;

/**
 * Một mục trong từ điển CC-CEDICT.
 *
 * <p>Nguồn: CC-CEDICT (MDBG) — giấy phép CC BY-SA 4.0, https://www.mdbg.net/chinese/dictionary?page=cc-cedict
 *
 * @param traditional    chữ phồn thể
 * @param simplified     chữ giản thể
 * @param pinyinNumbered pinyin dạng số như trong file gốc, ví dụ {@code "xue2 xi2"}
 * @param pinyinMarked   pinyin có dấu, không khoảng trắng, ví dụ {@code "xuéxí"}
 * @param definitions    các nghĩa tiếng Anh (mỗi phần tử là một nghĩa, đã bỏ dấu {@code /} bao quanh)
 */
public record CedictEntry(String traditional,
                          String simplified,
                          String pinyinNumbered,
                          String pinyinMarked,
                          List<String> definitions) {
}
