package com.example.bewebtiengtrung.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Trả về lỗi 403 dạng JSON (RFC 7807) khi người dùng đã đăng nhập nhưng không đủ quyền
 * (ví dụ tài khoản thường gọi vào nhóm endpoint {@code /api/v1/admin/**}).
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "about:blank");
        body.put("title", "Không có quyền");
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("detail", "Bạn không có quyền thực hiện thao tác này.");
        body.put("instance", request.getRequestURI());
        body.put("code", "FORBIDDEN");
        body.put("timestamp", Instant.now().toString());
        body.put("path", request.getRequestURI());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
