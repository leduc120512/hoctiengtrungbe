package com.example.bewebtiengtrung.module.wordimport.dto;

/**
 * Hành động áp dụng cho một dòng khi xác nhận nhập.
 *
 * <ul>
 *   <li>{@code CREATE} — tạo từ mới trong bảng words.</li>
 *   <li>{@code UPDATE} — ghi đè các trường khác null lên từ đã có ({@code existingWordId}).</li>
 *   <li>{@code LINK} — không đụng bảng words, chỉ đánh dấu từ đã có là đã học.</li>
 *   <li>{@code SKIP} — bỏ qua dòng này.</li>
 *   <li>{@code NEEDS_INPUT} — còn thiếu dữ liệu (ví dụ chưa chọn chữ Hán); confirm coi như SKIP.</li>
 * </ul>
 */
public enum ImportAction {
    CREATE, UPDATE, LINK, SKIP, NEEDS_INPUT
}
