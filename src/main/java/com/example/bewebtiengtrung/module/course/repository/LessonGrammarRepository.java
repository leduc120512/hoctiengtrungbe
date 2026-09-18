package com.example.bewebtiengtrung.module.course.repository;

import com.example.bewebtiengtrung.module.course.entity.LessonGrammar;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Truy vấn điểm ngữ pháp của bài học. */
@Repository
public interface LessonGrammarRepository extends JpaRepository<LessonGrammar, Long> {

    List<LessonGrammar> findByLessonIdOrderBySortOrderAscIdAsc(Long lessonId);

    /** Nạp kèm bài học để kiểm tra quyền/ràng buộc mà không sinh thêm truy vấn. */
    @EntityGraph(attributePaths = {"lesson"})
    Optional<LessonGrammar> findWithLessonById(Long id);
}
