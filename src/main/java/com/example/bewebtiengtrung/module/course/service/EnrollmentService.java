package com.example.bewebtiengtrung.module.course.service;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.common.dto.PageResponse;
import com.example.bewebtiengtrung.module.course.dto.EnrollmentResponse;
import org.springframework.data.domain.Pageable;

/** Nghiệp vụ ghi danh khóa học của người dùng đang đăng nhập. */
public interface EnrollmentService {

    /** Ghi danh vào khóa học; ghi danh trùng sẽ ném ConflictException. */
    MessageResponse enroll(Long courseId);

    /** Hủy ghi danh khỏi khóa học. */
    MessageResponse unenroll(Long courseId);

    /** Danh sách khóa học mà người dùng hiện tại đã ghi danh. */
    PageResponse<EnrollmentResponse> myEnrollments(Pageable pageable);
}
