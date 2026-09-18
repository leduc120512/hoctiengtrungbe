package com.example.bewebtiengtrung.module.sentence.service;

import com.example.bewebtiengtrung.module.sentence.dto.AiStatusResponse;
import com.example.bewebtiengtrung.module.sentence.dto.BulkSentenceRequest;
import com.example.bewebtiengtrung.module.sentence.dto.BulkSentenceResponse;
import com.example.bewebtiengtrung.module.sentence.dto.GenerateSentencesRequest;
import com.example.bewebtiengtrung.module.sentence.dto.GenerateSentencesResponse;
import com.example.bewebtiengtrung.module.sentence.dto.SentenceResponse;
import com.example.bewebtiengtrung.module.sentence.entity.SentenceSource;

import java.util.List;

/**
 * Nghiệp vụ kho "Câu của tôi": câu luyện nghe ghép từ đúng những từ người dùng đã học.
 *
 * <p>{@code userId} do controller lấy từ {@code SecurityUtils.currentUserId()} và truyền xuống.</p>
 */
public interface SentenceService {

    /** Toàn bộ câu của người dùng, sắp theo cấp rồi theo id. */
    List<SentenceResponse> listAll(Long userId);

    /**
     * Thêm nhiều câu một lần với nguồn {@code source}; câu trùng {@code hanzi_key}
     * (với câu đã có hoặc trong cùng đợt) bị bỏ qua và đếm vào {@code duplicates}.
     */
    BulkSentenceResponse addBulk(Long userId, BulkSentenceRequest request, SentenceSource source);

    /**
     * Xoá một câu.
     *
     * @throws com.example.bewebtiengtrung.common.exception.NotFoundException  không có câu với id này
     * @throws com.example.bewebtiengtrung.common.exception.ForbiddenException câu không thuộc người dùng
     */
    void delete(Long userId, Long id);

    /**
     * Xoá mọi câu của người dùng theo nguồn (không cho xoá {@link SentenceSource#BUILTIN}).
     *
     * @return số câu đã xoá
     */
    int deleteBySource(Long userId, SentenceSource source);

    /**
     * Nhờ AI sinh câu mới từ vốn từ đã học: lọc chữ lạ, chống trùng, gọi lại tối đa một lần
     * khi thiếu, rồi lưu với nguồn {@link SentenceSource#AI}.
     */
    GenerateSentencesResponse generate(Long userId, GenerateSentencesRequest request);

    /** Trạng thái cấu hình AI trên máy chủ (đã có API key hay chưa, model nào). */
    AiStatusResponse aiStatus();
}
