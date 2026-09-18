package com.example.bewebtiengtrung.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Ngoại lệ nghiệp vụ gốc của toàn hệ thống.
 *
 * <p>Service ném các lớp con của nó, {@code GlobalExceptionHandler} sẽ chuyển thành
 * {@code ProblemDetail} theo chuẩn RFC 7807. Không bao giờ để stack trace lọt ra client.</p>
 */
public class ApiException extends RuntimeException {

    /** Mã HTTP tương ứng sẽ trả về cho client. */
    private final HttpStatus status;

    /** Mã lỗi dạng chuỗi để client bắt theo nghiệp vụ, ví dụ {@code NOT_FOUND}. */
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public ApiException(HttpStatus status, String code, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
