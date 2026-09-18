package com.example.bewebtiengtrung.module.wordimport.dto;

import java.util.List;

/**
 * Kết quả thực thi nhập từ vựng.
 *
 * @param markedLearned số từ đã được đánh dấu đã học (0 nếu {@code markAsLearned = false})
 * @param wordIds       id của mọi từ vừa CREATE/UPDATE/LINK thành công, theo thứ tự dòng
 * @param errors        lỗi theo từng dòng (rỗng nếu tất cả thành công)
 */
public record ImportConfirmResponse(
        int created,
        int updated,
        int linked,
        int skipped,
        int markedLearned,
        List<Long> wordIds,
        List<ImportRowError> errors
) {
}
