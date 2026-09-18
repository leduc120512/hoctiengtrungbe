package com.example.bewebtiengtrung.module.ai.service;

import com.example.bewebtiengtrung.module.ai.dto.CompletedWord;

import java.util.List;

/**
 * Cổng gọi AI điền đủ chữ Hán / pinyin / nghĩa cho danh sách từ người học gõ tuỳ tiện.
 *
 * <p>Người học có thể gõ "học", "xuéxí", "学习", "học | xuéxí", hay cả "gợi ý 10 từ về đồ ăn";
 * AI trả về từng từ đủ bốn phần. Kết quả sau đó đi qua đúng bước duyệt của {@code ImportService}
 * (tra CC-CEDICT, đối chiếu hệ thống) nên AI sai thì người học vẫn thấy và sửa được.</p>
 *
 * <p>Tách interface để tầng service kiểm thử được bằng Mockito mà không cần API key thật.</p>
 */
public interface AiWordCompleter {

    /** Đã cấu hình API key hay chưa. */
    boolean isEnabled();

    /** Tên model sẽ dùng. */
    String model();

    /**
     * Điền đủ bốn phần cho danh sách từ.
     *
     * @param text         nội dung thô người học dán (nhiều dòng)
     * @param hskLevel     cấp HSK mục tiêu, để AI chọn từ thông dụng đúng trình độ khi tiếng Việt mơ hồ
     * @param learnedHanzi chữ Hán các từ đã học — để AI không gợi ý lại khi người học xin gợi ý
     * @param maxWords     số từ tối đa AI được trả về
     * @throws com.example.bewebtiengtrung.common.exception.ApiException 503 khi AI chưa cấu hình,
     *         429 khi quá tải, 502 khi dịch vụ AI lỗi
     */
    List<CompletedWord> completeWords(String text, int hskLevel, List<String> learnedHanzi, int maxWords);
}
