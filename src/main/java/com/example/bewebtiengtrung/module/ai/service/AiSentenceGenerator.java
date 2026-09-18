package com.example.bewebtiengtrung.module.ai.service;

import com.example.bewebtiengtrung.module.ai.dto.GeneratedSentence;
import com.example.bewebtiengtrung.module.ai.dto.LearnedWordBrief;

import java.util.List;

/**
 * Cổng gọi AI sinh câu luyện nghe từ vốn từ đã học.
 *
 * <p>Tách interface để {@code SentenceServiceImpl} kiểm thử được bằng Mockito
 * mà không cần API key thật.</p>
 */
public interface AiSentenceGenerator {

    /** Đã cấu hình API key hay chưa. */
    boolean isEnabled();

    /** Tên model sẽ dùng. */
    String model();

    /**
     * Trả về ứng viên THÔ (chưa lọc).
     *
     * @param words      từ đã học
     * @param avoidHanzi câu đã có (chỉ chữ Hán) — yêu cầu AI đừng tạo lại
     * @param count      số câu cần
     * @param level      cấp độ 1..3, null = trộn đều 3 cấp
     * @param focusWords chữ Hán muốn lặp nhiều hơn (có thể null/rỗng)
     * @throws com.example.bewebtiengtrung.common.exception.ApiException 503 khi AI chưa cấu hình,
     *         429 khi quá tải, 502 khi dịch vụ AI lỗi
     */
    List<GeneratedSentence> generate(List<LearnedWordBrief> words, List<String> avoidHanzi,
                                     int count, Integer level, List<String> focusWords);
}
