package com.example.bewebtiengtrung.module.course.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Khóa học tiếng Trung — ánh xạ bảng {@code courses}.
 * Một khóa học gồm nhiều bài học ({@link Lesson}) được sắp xếp theo sortOrder.
 */
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {

    /** Định danh thân thiện dùng trên URL, duy nhất toàn hệ thống. */
    @Column(name = "slug", nullable = false, length = 150, unique = true)
    private String slug;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** Mô tả dài — cột TEXT trong MySQL. */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    private CourseLevel level;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    /** BIT(1) trong MySQL — giữ kiểu Boolean (wrapper) theo quy ước của dự án. */
    @Column(name = "published", nullable = false)
    private Boolean published;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    /**
     * Danh sách bài học thuộc khóa học. Đây là quan hệ sở hữu thật sự nên dùng
     * cascade = ALL + orphanRemoval: xóa khóa học sẽ xóa toàn bộ bài học con.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC, id ASC")
    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>();

    /** Tiện ích gắn hai chiều để Hibernate ghi đúng khóa ngoại lessons.course_id. */
    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
        lesson.setCourse(this);
    }

    public void removeLesson(Lesson lesson) {
        this.lessons.remove(lesson);
        lesson.setCourse(null);
    }
}
