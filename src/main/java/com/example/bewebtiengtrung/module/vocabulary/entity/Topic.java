package com.example.bewebtiengtrung.module.vocabulary.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Chủ đề từ vựng — ánh xạ bảng {@code topics}.
 * Bảng có cột created_at / updated_at nên kế thừa {@link BaseEntity}.
 */
@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic extends BaseEntity {

    /** Đường dẫn thân thiện, duy nhất trong toàn hệ thống. */
    @Column(name = "slug", nullable = false, length = 120, unique = true)
    private String slug;

    /** Tên chủ đề bằng tiếng Việt. */
    @Column(name = "name_vi", nullable = false, length = 150)
    private String nameVi;

    /** Tên chủ đề bằng tiếng Trung. */
    @Column(name = "name_zh", length = 150)
    private String nameZh;

    /** Mô tả ngắn về chủ đề. */
    @Column(name = "description", length = 500)
    private String description;

    /** Tên icon dùng để hiển thị trên giao diện. */
    @Column(name = "icon", length = 100)
    private String icon;

    /** Thứ tự sắp xếp khi hiển thị danh sách chủ đề. */
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
