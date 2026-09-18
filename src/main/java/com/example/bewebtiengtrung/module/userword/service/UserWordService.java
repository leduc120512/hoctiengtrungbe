package com.example.bewebtiengtrung.module.userword.service;

import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.userword.dto.UpsertUserWordRequest;
import com.example.bewebtiengtrung.module.userword.dto.UserWordResponse;
import com.example.bewebtiengtrung.module.userword.dto.UserWordStatsResponse;
import com.example.bewebtiengtrung.module.userword.entity.UserWordStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Nghiệp vụ sổ từ đã học.
 *
 * <p>Mọi phương thức nhận {@code userId} tường minh (thay vì tự đọc SecurityContext)
 * vì các module khác — nhập từ vựng, sinh câu bằng AI — cũng gọi tới đây.
 * Controller truyền {@code SecurityUtils.currentUserId()}.</p>
 */
public interface UserWordService {

    /**
     * Danh sách từ trong sổ, có phân trang, với ba bộ lọc tuỳ chọn.
     * Không có sort trong {@code pageable} thì mặc định từ mới thêm lên trước.
     *
     * @param status   lọc theo trạng thái, null = tất cả
     * @param hskLevel lọc theo cấp HSK của từ gốc, null = tất cả
     * @param q        từ khoá khớp chữ giản thể / pinyin / nghĩa tiếng Việt, null hoặc rỗng = bỏ qua
     */
    PageResponse<UserWordResponse> list(Long userId, UserWordStatus status, Integer hskLevel, String q, Pageable pageable);

    /**
     * Thêm một từ vào sổ, hoặc cập nhật trạng thái / ghi chú nếu đã có.
     *
     * @throws com.example.bewebtiengtrung.common.exception.NotFoundException khi {@code wordId} không tồn tại
     */
    UserWordResponse upsert(Long userId, Long wordId, UpsertUserWordRequest request);

    /**
     * Đánh dấu hàng loạt từ với cùng một trạng thái. Idempotent: từ đã có trong sổ được
     * giữ nguyên (không hạ bậc MASTERED xuống LEARNED), chỉ tạo mới những từ còn thiếu.
     * Id không tồn tại trong bảng words bị bỏ qua.
     *
     * @return số dòng tạo mới
     */
    int bulkUpsert(Long userId, List<Long> wordIds, UserWordStatus status);

    /**
     * Gỡ một từ khỏi sổ.
     *
     * @throws com.example.bewebtiengtrung.common.exception.NotFoundException khi từ chưa có trong sổ
     */
    void remove(Long userId, Long wordId);

    /** Thống kê: tổng, theo trạng thái, theo cấp HSK, số từ học trong 7 ngày gần nhất. */
    UserWordStatsResponse stats(Long userId);

    /** Mọi {@code word_id} có trong sổ của người dùng, không phân biệt trạng thái. */
    List<Long> learnedWordIds(Long userId);
}
