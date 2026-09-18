package com.example.bewebtiengtrung.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Xung đột dữ liệu — bản ghi đã tồn tại hoặc vi phạm ràng buộc duy nhất (HTTP CONFLICT).
 */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, "CONFLICT", message);
    }

    public ConflictException(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, "CONFLICT", message, cause);
    }
}
