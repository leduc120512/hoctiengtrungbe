package com.example.bewebtiengtrung.module.vocabulary.service;

import com.example.bewebtiengtrung.module.vocabulary.dto.CreateTopicRequest;
import com.example.bewebtiengtrung.module.vocabulary.dto.TopicResponse;
import com.example.bewebtiengtrung.module.vocabulary.dto.UpdateTopicRequest;

import java.util.List;

/**
 * Nghiệp vụ quản lý chủ đề từ vựng.
 */
public interface TopicService {

    /** Lấy toàn bộ chủ đề đã sắp xếp theo thứ tự hiển thị. */
    List<TopicResponse> findAll();

    /** Lấy chi tiết một chủ đề theo slug. */
    TopicResponse findBySlug(String slug);

    /** Tạo chủ đề mới; ném ConflictException nếu slug đã tồn tại. */
    TopicResponse create(CreateTopicRequest request);

    /** Cập nhật chủ đề; ném ConflictException nếu slug đã thuộc về chủ đề khác. */
    TopicResponse update(Long id, UpdateTopicRequest request);

    /** Xoá chủ đề và gỡ liên kết của nó khỏi mọi từ vựng. */
    void delete(Long id);
}
