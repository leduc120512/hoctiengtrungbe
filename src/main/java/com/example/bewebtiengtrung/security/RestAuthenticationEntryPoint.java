package com.example.bewebtiengtrung.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Trả về lỗi 401 dạng JSON (RFC 7807) khi request chưa được xác thực,
 * thay cho hành vi mặc định của Spring Security là chuyển hướng tới trang đăng nhập.
 *
 * <p>Điểm này nằm TRƯỚC DispatcherServlet nên {@code GlobalExceptionHandler} không xử lý được,
 * vì vậy phải tự dựng body JSON tại đây (Jackson 3: {@code tools.jackson.databind}).</p>
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "about:blank");
        body.put("title", "Chưa xác thực");
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("detail", "Bạn cần đăng nhập để sử dụng chức năng này.");
        body.put("instance", request.getRequestURI());
        body.put("code", "UNAUTHORIZED");
        body.put("timestamp", Instant.now().toString());
        body.put("path", request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
