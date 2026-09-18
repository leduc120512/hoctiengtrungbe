package com.example.bewebtiengtrung.module.sentence.entity;

/**
 * Nguồn gốc của một câu trong kho "Câu của tôi".
 *
 * <ul>
 *   <li>{@link #BUILTIN} — câu seed sẵn (90 câu gốc của trang), không cho xoá hàng loạt.</li>
 *   <li>{@link #MANUAL} — người dùng tự dán vào.</li>
 *   <li>{@link #AI} — Claude sinh ra từ vốn từ đã học.</li>
 * </ul>
 */
public enum SentenceSource {
    BUILTIN,
    MANUAL,
    AI
}
