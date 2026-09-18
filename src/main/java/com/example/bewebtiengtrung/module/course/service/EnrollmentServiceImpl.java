package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.common.exception.ConflictException;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.module.course.dto.EnrollmentResponse;
import com.example.bewebtiengtrung.module.course.entity.Course;
import com.example.bewebtiengtrung.module.course.entity.Enrollment;
import com.example.bewebtiengtrung.module.course.mapper.EnrollmentMapper;
import com.example.bewebtiengtrung.module.course.repository.CourseRepository;
import com.example.bewebtiengtrung.module.course.repository.EnrollmentRepository;
import com.example.bewebtiengtrung.module.user.repository.UserRepository;
import com.example.bewebtiengtrung.security.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/** Cài đặt nghiệp vụ ghi danh khóa học. */
@Service
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 CourseRepository courseRepository,
                                 UserRepository userRepository,
                                 EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Override
    @Transactional
    public MessageResponse enroll(Long courseId) {
        Long userId = SecurityUtils.currentUserId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học với id: " + courseId));

        // Người dùng thường chỉ được ghi danh vào khóa học đã xuất bản.
        if (!SecurityUtils.isAdmin() && !Boolean.TRUE.equals(course.getPublished())) {
            throw new NotFoundException("Không tìm thấy khóa học với id: " + courseId);
        }
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new ConflictException("Bạn đã ghi danh khóa học này rồi");
        }

        Enrollment enrollment = new Enrollment();
        // getReferenceById trả về proxy nên không cần thêm truy vấn chỉ để gán khóa ngoại.
        enrollment.setUser(userRepository.getReferenceById(userId));
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(Instant.now());
        enrollmentRepository.save(enrollment);

        return new MessageResponse("Ghi danh khóa học thành công");
    }

    @Override
    @Transactional
    public MessageResponse unenroll(Long courseId) {
        Long userId = SecurityUtils.currentUserId();
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new NotFoundException("Bạn chưa ghi danh khóa học này"));
        enrollmentRepository.delete(enrollment);
        return new MessageResponse("Hủy ghi danh khóa học thành công");
    }

    @Override
    public PageResponse<EnrollmentResponse> myEnrollments(Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        return PageResponse.of(enrollmentRepository.findByUserId(userId, pageable)
                .map(enrollmentMapper::toResponse));
    }
}
