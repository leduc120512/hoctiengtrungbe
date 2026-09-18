package com.example.bewebtiengtrung.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bộ xử lý lỗi tập trung cho toàn bộ REST API.
 *
 * <p>Mọi lỗi đều được quy về {@link ProblemDetail} theo chuẩn RFC 7807
 * (content-type {@code application/problem+json}) kèm các trường mở rộng:</p>
 * <ul>
 *   <li>{@code code} — mã lỗi nghiệp vụ để client bắt theo logic</li>
 *   <li>{@code timestamp} — thời điểm xảy ra lỗi (UTC)</li>
 *   <li>{@code path} — đường dẫn đã gọi</li>
 *   <li>{@code errors} — map "tên trường → thông báo" khi lỗi validation</li>
 * </ul>
 *
 * <p>Không bao giờ trả stack trace về client; lỗi ngoài dự kiến được ghi log ở mức ERROR.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Kiểu URI mặc định của ProblemDetail khi không có tài liệu lỗi riêng. */
    private static final URI DEFAULT_TYPE = URI.create("about:blank");

    private static final String INVALID_VALUE = "Giá trị không hợp lệ";

    // ------------------------------------------------------------------
    // Lỗi nghiệp vụ do service chủ động ném ra
    // ------------------------------------------------------------------

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ProblemDetail> handleApiException(ApiException ex, HttpServletRequest request) {
        ProblemDetail problem = build(ex.getStatus(), titleOf(ex.getStatus()), ex.getMessage(), ex.getCode(), request);
        if (ex.getStatus().is5xxServerError()) {
            log.error("Lỗi nghiệp vụ 5xx tại {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        } else {
            log.debug("Lỗi nghiệp vụ {} tại {}: {}", ex.getStatus().value(), request.getRequestURI(), ex.getMessage());
        }
        return toResponse(problem);
    }

    // ------------------------------------------------------------------
    // Lỗi validation
    // ------------------------------------------------------------------

    /** Lỗi của {@code @Valid} trên {@code @RequestBody}. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                     HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(),
                    fieldError.getDefaultMessage() == null ? INVALID_VALUE : fieldError.getDefaultMessage());
        }
        ex.getBindingResult().getGlobalErrors().forEach(globalError ->
                errors.putIfAbsent(globalError.getObjectName(),
                        globalError.getDefaultMessage() == null ? INVALID_VALUE : globalError.getDefaultMessage()));

        ProblemDetail problem = build(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ",
                "Dữ liệu gửi lên không hợp lệ, vui lòng kiểm tra lại các trường bị lỗi.",
                "VALIDATION_ERROR", request);
        problem.setProperty("errors", errors);
        return toResponse(problem);
    }

    /** Lỗi ràng buộc trên tham số ({@code @RequestParam}, {@code @PathVariable}) hoặc ở tầng persistence. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (ex.getConstraintViolations() != null) {
            for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
                String field = violation.getPropertyPath() == null
                        ? "request"
                        : violation.getPropertyPath().toString();
                errors.putIfAbsent(lastPathSegment(field), violation.getMessage());
            }
        }
        ProblemDetail problem = build(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ",
                "Tham số gửi lên không hợp lệ, vui lòng kiểm tra lại.",
                "VALIDATION_ERROR", request);
        problem.setProperty("errors", errors);
        return toResponse(problem);
    }

    /** Body JSON sai cú pháp hoặc sai kiểu dữ liệu. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        log.debug("Không đọc được body request tại {}: {}", request.getRequestURI(), ex.getMessage());
        return toResponse(build(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ",
                "Nội dung JSON gửi lên không đọc được hoặc sai định dạng.",
                "MALFORMED_REQUEST", request));
    }

    /** Thiếu tham số bắt buộc trên query string. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest request) {
        ProblemDetail problem = build(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ",
                "Thiếu tham số bắt buộc: " + ex.getParameterName(), "MISSING_PARAMETER", request);
        problem.setProperty("errors", Map.of(ex.getParameterName(), "Tham số này là bắt buộc"));
        return toResponse(problem);
    }

    /**
     * Lỗi ràng buộc jakarta đặt trực tiếp trên tham số của controller
     * (Spring 6.1+ ném {@link HandlerMethodValidationException} thay cho ConstraintViolationException).
     * Nếu không bắt ở đây, lỗi sẽ rơi xuống handler tổng và biến thành 500.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ProblemDetail> handleHandlerMethodValidation(HandlerMethodValidationException ex,
                                                                       HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        int index = 0;
        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            String name = result.getMethodParameter().getParameterName();
            if (name == null || name.isBlank()) {
                name = "arg" + index;
            }
            index++;
            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                errors.putIfAbsent(name,
                        error.getDefaultMessage() == null ? INVALID_VALUE : error.getDefaultMessage());
            }
        }
        ProblemDetail problem = build(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ",
                "Tham số gửi lên không hợp lệ, vui lòng kiểm tra lại.",
                "VALIDATION_ERROR", request);
        problem.setProperty("errors", errors);
        return toResponse(problem);
    }

    /** Content-Type của request không được hỗ trợ — 415. */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpServletRequest request) {
        return toResponse(build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Định dạng không được hỗ trợ",
                "Kiểu dữ liệu gửi lên không được hỗ trợ, vui lòng dùng application/json.",
                "UNSUPPORTED_MEDIA_TYPE", request));
    }

    /** Client yêu cầu định dạng trả về mà máy chủ không tạo được — 406. */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ProblemDetail> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                      HttpServletRequest request) {
        return toResponse(build(HttpStatus.NOT_ACCEPTABLE, "Định dạng không phù hợp",
                "Máy chủ không thể trả về dữ liệu theo định dạng bạn yêu cầu.",
                "NOT_ACCEPTABLE", request));
    }

    /** Tham số sai kiểu, ví dụ id truyền vào không phải số. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        ProblemDetail problem = build(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ",
                "Giá trị của tham số [" + ex.getName() + "] không đúng kiểu dữ liệu mong đợi.",
                "TYPE_MISMATCH", request);
        problem.setProperty("errors", Map.of(ex.getName(), "Giá trị không đúng định dạng"));
        return toResponse(problem);
    }

    // ------------------------------------------------------------------
    // Bảo mật
    // ------------------------------------------------------------------

    /** Chưa xác thực hoặc token hỏng — 401. */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(AuthenticationException ex,
                                                              HttpServletRequest request) {
        log.debug("Xác thực thất bại tại {}: {}", request.getRequestURI(), ex.getMessage());
        return toResponse(build(HttpStatus.UNAUTHORIZED, "Chưa xác thực",
                "Bạn cần đăng nhập để sử dụng chức năng này.", "UNAUTHORIZED", request));
    }

    /** Đã đăng nhập nhưng thiếu quyền — 403. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest request) {
        log.debug("Từ chối truy cập tại {}: {}", request.getRequestURI(), ex.getMessage());
        return toResponse(build(HttpStatus.FORBIDDEN, "Không có quyền",
                "Bạn không có quyền thực hiện thao tác này.", "FORBIDDEN", request));
    }

    // ------------------------------------------------------------------
    // Dữ liệu / định tuyến
    // ------------------------------------------------------------------

    /** Vi phạm ràng buộc DB (unique, khoá ngoại...) — 409. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex,
                                                             HttpServletRequest request) {
        log.warn("Vi phạm ràng buộc dữ liệu tại {}: {}", request.getRequestURI(), rootMessage(ex));
        return toResponse(build(HttpStatus.CONFLICT, "Xung đột dữ liệu",
                "Dữ liệu đã tồn tại hoặc vi phạm ràng buộc toàn vẹn của hệ thống.",
                "DATA_INTEGRITY_VIOLATION", request));
    }

    /** Không tìm thấy endpoint / tài nguyên tĩnh tương ứng — 404. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFound(NoResourceFoundException ex,
                                                               HttpServletRequest request) {
        log.debug("Không tìm thấy tài nguyên {}: {}", request.getRequestURI(), ex.getMessage());
        return toResponse(build(HttpStatus.NOT_FOUND, "Không tìm thấy",
                "Không tìm thấy đường dẫn: " + request.getRequestURI(), "NOT_FOUND", request));
    }

    /** Sai HTTP method — 405. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                  HttpServletRequest request) {
        return toResponse(build(HttpStatus.METHOD_NOT_ALLOWED, "Phương thức không được hỗ trợ",
                "Phương thức " + ex.getMethod() + " không được hỗ trợ cho đường dẫn này.",
                "METHOD_NOT_ALLOWED", request));
    }

    /**
     * Các ngoại lệ chuẩn của Spring MVC đã mang sẵn mã trạng thái
     * ({@code ResponseStatusException} và họ hàng). Giữ nguyên mã đó thay vì để rơi
     * xuống handler tổng và bị đổi thành 500.
     */
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ProblemDetail> handleErrorResponse(ErrorResponseException ex,
                                                             HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        if (status.is5xxServerError()) {
            log.error("Lỗi {} tại {}: {}", status.value(), request.getRequestURI(), ex.getMessage(), ex);
        }
        String detail = ex.getBody() != null && ex.getBody().getDetail() != null
                ? ex.getBody().getDetail()
                : status.getReasonPhrase();
        return toResponse(build(status, titleOf(status), detail, status.name(), request));
    }

    // ------------------------------------------------------------------
    // Bắt tất cả lỗi còn lại
    // ------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Lỗi không xác định tại {} {}", request.getMethod(), request.getRequestURI(), ex);
        return toResponse(build(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống",
                "Đã có lỗi xảy ra ở phía máy chủ. Vui lòng thử lại sau.",
                "INTERNAL_ERROR", request));
    }

    // ------------------------------------------------------------------
    // Tiện ích nội bộ
    // ------------------------------------------------------------------

    private ProblemDetail build(HttpStatus status, String title, String detail, String code,
                                HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status,
                detail == null ? status.getReasonPhrase() : detail);
        problem.setType(DEFAULT_TYPE);
        problem.setTitle(title);
        problem.setProperty("code", code);
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    private ResponseEntity<ProblemDetail> toResponse(ProblemDetail problem) {
        return ResponseEntity.status(problem.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    private String titleOf(HttpStatus status) {
        switch (status) {
            case NOT_FOUND:
                return "Không tìm thấy";
            case BAD_REQUEST:
                return "Yêu cầu không hợp lệ";
            case CONFLICT:
                return "Xung đột dữ liệu";
            case FORBIDDEN:
                return "Không có quyền";
            case UNAUTHORIZED:
                return "Chưa xác thực";
            default:
                return status.getReasonPhrase();
        }
    }

    /** Lấy đoạn cuối của property path (ví dụ {@code createWord.arg0.pinyin} → {@code pinyin}). */
    private String lastPathSegment(String path) {
        int idx = path.lastIndexOf('.');
        return idx >= 0 && idx < path.length() - 1 ? path.substring(idx + 1) : path;
    }

    /** Lấy message của nguyên nhân gốc để ghi log (không bao giờ trả ra ngoài client). */
    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current.getMessage();
    }
}
