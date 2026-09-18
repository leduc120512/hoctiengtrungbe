package com.example.bewebtiengtrung.module.wordimport.dto;

/**
 * Trạng thái của một dòng sau khi duyệt trước.
 *
 * <ul>
 *   <li>{@code OK} — hợp lệ, sẽ tạo mới.</li>
 *   <li>{@code EXISTS} — đã có trong bảng words (cùng chữ Hán + cùng pinyin), chỉ cần liên kết.</li>
 *   <li>{@code WARNING} — cần người dùng xem lại (thiếu chữ Hán, lệch thanh điệu, không có trong từ điển).</li>
 *   <li>{@code ERROR} — không thể nhập (thiếu dữ liệu bắt buộc, trùng trong lô).</li>
 * </ul>
 */
public enum ImportRowStatus {
    OK, EXISTS, WARNING, ERROR
}
