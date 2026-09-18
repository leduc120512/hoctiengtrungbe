package com.example.bewebtiengtrung.module.srs.service;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.module.srs.dto.ReviewCardResponse;
import com.example.bewebtiengtrung.module.srs.dto.ReviewRequest;
import com.example.bewebtiengtrung.module.srs.dto.ReviewStateResponse;
import com.example.bewebtiengtrung.module.srs.dto.SrsStatsResponse;

import java.util.List;

/** Nghiệp vụ ôn tập giãn cách (SM-2) của người dùng hiện tại. */
public interface SrsService {

    /**
     * Đăng ký học một bộ thẻ: tạo trạng thái ôn tập cho mọi thẻ trong deck mà
     * người dùng chưa có. Thao tác idempotent – gọi nhiều lần không tạo bản ghi trùng.
     */
    MessageResponse subscribe(Long deckId);

    /** Danh sách thẻ đã đến hạn ôn, sắp theo hạn tăng dần. */
    List<ReviewCardResponse> getDue(Long deckId, int limit);

    /** Chấm điểm một lần ôn: chạy SM-2, lưu trạng thái và ghi nhật ký. */
    ReviewStateResponse review(ReviewRequest request);

    /** Thống kê tiến độ ôn tập. */
    SrsStatsResponse stats();
}
