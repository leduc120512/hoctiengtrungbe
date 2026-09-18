package com.example.bewebtiengtrung.module.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Điểm ngữ pháp của một bài học — ánh xạ bảng {@code lesson_grammar}.
 * Bảng này KHÔNG có cột created_at/updated_at nên entity không kế thừa BaseEntity
 * mà khai báo khóa chính tường minh.
 */
@Entity
@Table(name = "lesson_grammar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonGrammar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** Công thức ngữ pháp, ví dụ dạng chuỗi mẫu câu. */
    @Column(name = "structure", length = 500)
    private String structure;

    /** Giải thích chi tiết bằng tiếng Việt — cột TEXT. */
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "example_zh", length = 500)
    private String exampleZh;

    @Column(name = "example_vi", length = 800)
    private String exampleVi;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
