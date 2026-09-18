package com.example.bewebtiengtrung.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Chưa đăng nhập hoặc token không hợp lệ / đã hết hạn (HTTP UNAUTHORIZED).
 */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message, cause);
    }
}
