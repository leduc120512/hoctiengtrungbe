package com.example.bewebtiengtrung.module.dictionary.service;

import java.util.List;

/**
 * Từ điển Trung–Anh CC-CEDICT nạp sẵn trong bộ nhớ.
 *
 * <p>Dùng cho ba việc: kiểm tra chữ Hán/pinyin người dùng nhập có đúng không, tự điền nghĩa tiếng Anh,
 * và tra từ nhanh Anh ↔ Trung. Toàn bộ dữ liệu bất biến sau khi nạp nên các phương thức đều an toàn
 * khi gọi đồng thời.
 */
public interface CedictService {

    /** Mọi mục có chữ giản thể đúng bằng {@code simplified} (từ đa âm → nhiều mục). Rỗng nếu không có. */
    List<CedictEntry> lookupSimplified(String simplified);

    /**
     * Tra ngược theo pinyin ở bất kỳ dạng nào (có dấu, dạng số, có/không khoảng trắng).
     * Kết quả đã xếp hạng: mục có trong bảng {@code words} của hệ thống lên trước; các mục chỉ là
     * biến thể/họ/cách viết cũ xuống cuối; chữ ngắn hơn lên trước. Tối đa {@code limit} mục.
     */
    List<CedictEntry> reverseLookup(String pinyin, int limit);

    /**
     * Tìm theo nghĩa tiếng Anh (khớp từ nguyên vẹn, không phân biệt hoa thường) HOẶC theo chữ Hán
     * (giản thể/phồn thể bắt đầu bằng {@code q}). Ưu tiên: definition bằng đúng q → bắt đầu bằng q →
     * chứa q; mục có trong hệ thống lên trước; chữ ngắn hơn lên trước. Tối đa {@code limit} mục.
     */
    List<CedictEntry> search(String q, int limit);

    /** Nghĩa tiếng Anh gợi ý: nối tối đa 3 nghĩa đầu bằng {@code "; "}, bỏ đoạn {@code CL:…}, cắt ≤ 500 ký tự. */
    String suggestEnglish(CedictEntry entry);

    /** Số mục đã nạp. */
    int size();
}
