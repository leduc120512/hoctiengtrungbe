package com.example.bewebtiengtrung.module.course.mapper;

import com.example.bewebtiengtrung.module.course.dto.EnrollmentResponse;
import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.Enrollment;
import org.springframework.stereotype.Component;

/** Chuyển đổi entity Enrollment sang DTO trả về cho client. */
@Component
public class EnrollmentMapper {

    /** Yêu cầu quan hệ course đã được nạp sẵn (EntityGraph) để không sinh thêm truy vấn. */
    public EnrollmentResponse toResponse(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        return new EnrollmentResponse(
                enrollment.getId(),
                course.getId(),
                course.getSlug(),
                course.getTitle(),
                course.getLevel(),
                course.getThumbnailUrl(),
                enrollment.getEnrolledAt(),
                enrollment.getCompletedAt()
        );
    }
}
