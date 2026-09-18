package com.example.bewebtiengtrung.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Đã đăng nhập nhưng không có quyền thao tác trên tài nguyên này (HTTP FORBIDDEN).
 */
public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", message, cause);
    }
}
