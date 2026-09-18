package com.example.bewebtiengtrung.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Không tìm thấy tài nguyên được yêu cầu (HTTP NOT_FOUND).
 */
public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message, cause);
    }
}
