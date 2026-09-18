package com.example.bewebtiengtrung.module.course.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Bài học — ánh xạ bảng {@code lessons}.
 * Mỗi bài học thuộc về một khóa học, chứa các điểm ngữ pháp và một tập từ vựng.
 */
@Entity
@Table(
        name = "lessons",
        uniqueConstraints = @UniqueConstraint(name = "uk_lessons_course_slug", columnNames = {"course_id", "slug"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson extends BaseEntity {

    /** Luôn LAZY theo quy ước dự án để tránh tải thừa khóa học khi chỉ cần bài học. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** Slug chỉ duy nhất trong phạm vi một khóa học (uk_lessons_course_slug). */
    @Column(name = "slug", nullable = false, length = 150)
    private String slug;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "summary", length = 1000)
    private String summary;

    /** Nội dung bài học dạng HTML/Markdown — cột LONGTEXT. */
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "published", nullable = false)
    private Boolean published;

    /**
     * Các điểm ngữ pháp của bài học — con thật sự nên cascade ALL + orphanRemoval.
     *
     * BẮT BUỘC dùng {@link LinkedHashSet} chứ KHÔNG dùng List. EntityGraph của
     * {@code LessonRepository#findWithDetailsById} nạp đồng thời grammarPoints và words
     * bằng MỘT câu SQL, tạo ra tích Descartes giữa hai bảng con. Nếu để kiểu List thì
     * Hibernate coi đây là "bag" (không có @OrderColumn) và KHÔNG khử trùng lặp: mỗi điểm
     * ngữ pháp sẽ bị lặp lại đúng bằng số từ vựng của bài học rồi lọt thẳng ra response.
     * Set khử trùng lặp theo định danh entity nên kết quả luôn đúng, còn LinkedHashSet
     * vẫn giữ nguyên thứ tự do {@code @OrderBy} sinh ra.
     */
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC, id ASC")
    @Builder.Default
    private Set<LessonGrammar> grammarPoints = new LinkedHashSet<>();

    /**
     * Từ vựng của bài học — bảng nối {@code lesson_words(lesson_id, word_id, sort_order)}.
     *
     * LỰA CHỌN CÓ CHỦ Ý: bảng nối còn có thêm cột {@code sort_order} mà một quan hệ
     * {@code @ManyToMany} thuần KHÔNG thể ánh xạ được (muốn ánh xạ phải tách ra thành một
     * entity trung gian LessonWord với khóa chính ghép, làm phức tạp toàn bộ tầng service).
     * Ở đây ta chấp nhận đánh đổi: giữ {@code @ManyToMany} cho gọn và BỎ QUA cột
     * {@code sort_order} ở tầng JPA. Cách này an toàn vì cột có {@code DEFAULT 0}, nên câu
     * INSERT do Hibernate sinh ra (chỉ gồm lesson_id, word_id) vẫn chạy đúng, và
     * {@code ddl-auto=validate} không bắt lỗi cột thừa. Thứ tự hiển thị từ vựng do tầng
     * service quyết định (sắp theo id) thay vì đọc từ {@code sort_order}.
     *
     * Cũng như grammarPoints, đây phải là Set để tránh trùng lặp khi cả hai tập con được
     * fetch chung trong một EntityGraph.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lesson_words",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    @Builder.Default
    private Set<Word> words = new LinkedHashSet<>();

    /** Tiện ích gắn hai chiều cho điểm ngữ pháp. */
    public void addGrammarPoint(LessonGrammar grammar) {
        this.grammarPoints.add(grammar);
        grammar.setLesson(this);
    }

    public void removeGrammarPoint(LessonGrammar grammar) {
        this.grammarPoints.remove(grammar);
        grammar.setLesson(null);
    }
}
