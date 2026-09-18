package com.example.bewebtiengtrung.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bộ lọc đọc access token từ header {@code Authorization: Bearer <token>},
 * kiểm tra chữ ký rồi nạp người dùng vào {@code SecurityContext}.
 *
 * <p>Nguyên tắc quan trọng: token sai hoặc hết hạn KHÔNG được ném ngoại lệ ở đây.
 * Bộ lọc chỉ để {@code SecurityContext} trống và cho request đi tiếp; việc trả về 401
 * do {@code RestAuthenticationEntryPoint} đảm nhiệm nếu endpoint yêu cầu đăng nhập.</p>
 *
 * <p>Chỉ bỏ qua các đường dẫn tài liệu ({@code /swagger-ui/**}, {@code /v3/api-docs/**}).
 * TUYỆT ĐỐI KHÔNG bỏ qua {@code /api/v1/auth/**}: các endpoint {@code /auth/logout} và
 * {@code /auth/logout-all} tuy nằm trong nhánh permitAll nhưng vẫn cần danh tính người dùng
 * ({@code SecurityUtils.currentUserId()}); nếu bỏ lọc thì SecurityContext luôn rỗng và
 * hai endpoint này sẽ luôn trả về 401 dù client gửi access token hợp lệ.</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Các tiền tố đường dẫn không cần kiểm tra token (chỉ là tài liệu tĩnh của Swagger).
     * Không đưa {@code /api/v1/auth/} vào đây — xem phần mô tả của lớp.
     */
    private static final String[] SKIPPED_PREFIXES = {
            "/swagger-ui",
            "/v3/api-docs"
    };

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /** Không chạy bộ lọc cho các endpoint công khai để tiết kiệm truy vấn CSDL. */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = pathOf(request);
        for (String prefix : SKIPPED_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticate(token, request);
        }
        filterChain.doFilter(request, response);
    }

    /** Xác thực token và nạp người dùng; mọi lỗi chỉ được ghi log ở mức debug. */
    private void authenticate(String token, HttpServletRequest request) {
        try {
            Claims claims = jwtService.parseClaims(token);

            UserDetails userDetails = loadUser(claims);
            if (userDetails == null) {
                return;
            }
            if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
                log.debug("Tài khoản {} đang bị khoá hoặc vô hiệu hoá", userDetails.getUsername());
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        } catch (Exception ex) {
            // Token hỏng / hết hạn / người dùng đã bị xoá: bỏ qua, request đi tiếp không có danh tính
            SecurityContextHolder.clearContext();
            log.debug("Bỏ qua access token không hợp lệ tại {}: {}", request.getRequestURI(), ex.getMessage());
        }
    }

    /** Ưu tiên nạp theo email trong claim, thiếu email thì dùng {@code sub} (id người dùng). */
    private UserDetails loadUser(Claims claims) {
        String email = claims.get("email", String.class);
        if (email != null && !email.isBlank()) {
            return userDetailsService.loadUserByUsername(email);
        }
        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            return null;
        }
        return userDetailsService.loadUserById(Long.valueOf(subject));
    }

    /** Lấy phần token phía sau tiền tố {@code Bearer }. */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    /** Đường dẫn của request đã bỏ context path (ứng dụng thường chạy ở context rỗng). */
    private String pathOf(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }
}
