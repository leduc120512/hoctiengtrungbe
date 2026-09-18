package com.example.bewebtiengtrung.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Dữ liệu client gửi lên không hợp lệ về mặt nghiệp vụ (HTTP BAD_REQUEST).
 */
public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message, cause);
    }
}
