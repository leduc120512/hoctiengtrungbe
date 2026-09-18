package com.example.bewebtiengtrung.module.wordimport.service;

import com.example.bewebtiengtrung.module.wordimport.dto.ImportAiRequest;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportAiResponse;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmRequest;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportConfirmResponse;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewRequest;
import com.example.bewebtiengtrung.module.wordimport.dto.ImportPreviewResponse;

/**
 * Nghiệp vụ nhập từ vựng có duyệt: dán thô, preview (tra từ điển, đối chiếu hệ thống), rồi confirm.
 */
public interface ImportService {

    /**
     * Duyệt trước một lô từ: chuẩn hoá, tra CC-CEDICT, đối chiếu bảng words và phát hiện trùng trong lô.
     * Không ghi gì vào DB.
     *
     * @param userId người dùng hiện tại (để tính cờ {@code alreadyLearned})
     */
    ImportPreviewResponse preview(Long userId, ImportPreviewRequest request);

    /**
     * Nhờ AI điền đủ chữ Hán / pinyin / nghĩa cho nội dung thô (chỉ tiếng Việt, chỉ pinyin, hay một câu
     * "gợi ý 10 từ về…"), rồi duyệt trước đúng như {@link #preview}. Không ghi gì vào DB.
     *
     * @throws com.example.bewebtiengtrung.common.exception.ApiException 503 khi AI chưa cấu hình
     */
    ImportAiResponse completeWithAi(Long userId, ImportAiRequest request);

    /**
     * Thực thi các dòng đã duyệt. Mỗi dòng chạy trong transaction riêng; dòng lỗi được ghi vào
     * {@code errors} và không làm hỏng các dòng khác. Nếu {@code markAsLearned} thì mọi từ vừa
     * CREATE/UPDATE/LINK được đánh dấu đã học cho {@code userId}.
     */
    ImportConfirmResponse confirm(Long userId, ImportConfirmRequest request);
}
