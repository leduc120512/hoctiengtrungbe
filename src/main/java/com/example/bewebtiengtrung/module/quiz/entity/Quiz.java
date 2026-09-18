package com.example.bewebtiengtrung.module.quiz.entity;

import com.example.bewebtiengtrung.common.entity.BaseEntity;
import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.Lesson;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Đề kiểm tra - bảng {@code quizzes}.
 *
 * <p>Một đề có thể gắn với một khoá học hoặc một bài học (cả hai đều NULLABLE),
 * hoặc đứng độc lập theo cấp độ HSK.</p>
 */
@Entity
@Table(
        name = "quizzes",
        uniqueConstraints = @UniqueConstraint(name = "uk_quizzes_slug", columnNames = "slug")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz extends BaseEntity {

    /** Định danh thân thiện trên URL, duy nhất. */
    @Column(name = "slug", nullable = false, length = 150)
    private String slug;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Khoá học chứa đề thi - có thể NULL (đề thi độc lập). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    /** Bài học chứa đề thi - có thể NULL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** Cấp độ HSK (1..6), có thể NULL. */
    @Column(name = "hsk_level")
    private Integer hskLevel;

    /** Giới hạn thời gian làm bài tính bằng giây; NULL nghĩa là không giới hạn. */
    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    /** Ngưỡng phần trăm để đạt (0..100). */
    @Column(name = "pass_score", nullable = false)
    private Integer passScore;

    /** Chỉ đề đã publish mới hiển thị cho người dùng thường. */
    @Column(name = "published", nullable = false)
    private Boolean published;

    /** Danh sách câu hỏi - con thực sự của đề thi nên cascade ALL + orphanRemoval. */
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    /** Gắn câu hỏi vào đề và giữ đồng bộ hai chiều. */
    public void addQuestion(Question question) {
        this.questions.add(question);
        question.setQuiz(this);
    }

    /** Gỡ câu hỏi khỏi đề; orphanRemoval sẽ xoá bản ghi tương ứng. */
    public void removeQuestion(Question question) {
        this.questions.remove(question);
        question.setQuiz(null);
    }
}
