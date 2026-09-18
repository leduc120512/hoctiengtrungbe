package com.example.bewebtiengtrung.module.ai.dto;

/**
 * Một từ đã học, rút gọn để đưa vào prompt.
 *
 * @param hanzi     chữ Hán giản thể
 * @param pinyin    pinyin có dấu thanh
 * @param meaningVi nghĩa tiếng Việt
 */
public record LearnedWordBrief(String hanzi, String pinyin, String meaningVi) {
}
