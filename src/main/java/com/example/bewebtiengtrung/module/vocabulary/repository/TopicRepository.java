package com.example.bewebtiengtrung.module.vocabulary.repository;

import com.example.bewebtiengtrung.module.vocabulary.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Truy vấn dữ liệu chủ đề từ vựng.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    /** Tìm chủ đề theo slug (dùng cho endpoint công khai GET /topics/{slug}). */
    Optional<Topic> findBySlug(String slug);

    /** Kiểm tra slug đã tồn tại hay chưa — phục vụ kiểm tra trùng khi tạo mới. */
    boolean existsBySlug(String slug);

    /** Kiểm tra slug đã được một chủ đề khác sử dụng hay chưa — phục vụ cập nhật. */
    boolean existsBySlugAndIdNot(String slug, Long id);

    /** Lấy toàn bộ chủ đề, sắp xếp theo thứ tự hiển thị rồi tới tên tiếng Việt. */
    List<Topic> findAllByOrderBySortOrderAscNameViAsc();
}
