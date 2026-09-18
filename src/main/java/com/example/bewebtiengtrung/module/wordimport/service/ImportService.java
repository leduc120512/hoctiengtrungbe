package com.example.bewebtiengtrung.module.wordimport.service;

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
     * Thực thi các dòng đã duyệt. Mỗi dòng chạy trong transaction riêng; dòng lỗi được ghi vào
     * {@code errors} và không làm hỏng các dòng khác. Nếu {@code markAsLearned} thì mọi từ vừa
     * CREATE/UPDATE/LINK được đánh dấu đã học cho {@code userId}.
     */
    ImportConfirmResponse confirm(Long userId, ImportConfirmRequest request);
}
