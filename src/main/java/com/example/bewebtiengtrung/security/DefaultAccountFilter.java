package com.example.bewebtiengtrung.security;

import com.example.bewebtiengtrung.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Cho phần "Câu của tôi" chạy KHÔNG cần đăng nhập.
 *
 * <p>Đây là site cá nhân một người dùng: bước đăng nhập (kể cả tự đăng nhập bằng tài khoản khách)
 * chỉ làm trang chậm thêm một vòng mạng mà không bảo vệ gì. Bộ lọc này đứng ngay sau
 * {@link JwtAuthenticationFilter}: nếu request tới một trong các nhánh của phần "Câu của tôi"
 * ({@link #PREFIXES}) mà {@code SecurityContext} vẫn trống — không có token, hoặc token hỏng —
 * thì nạp tài khoản cấu hình ở {@code app.default-account.username} (mặc định {@code 2222}) vào
 * {@code SecurityContext}, y như người đó vừa đăng nhập. Request mang token hợp lệ không bị đụng tới.</p>
 *
 * <p>Tài khoản mặc định được nạp từ CSDL rồi giữ lại {@link #CACHE_TTL_MILLIS} để mỗi request không
 * tốn thêm hai truy vấn (theo email rồi theo tên đăng nhập) — với MySQL ở xa, đó chính là phần "lâu".
 * Tài khoản bị khoá / không tồn tại thì bộ lọc im lặng bỏ qua và request đi tiếp như khách vãng lai
 * (sẽ nhận 401 như cũ); lỗi chỉ ghi log WARN một lần cho mỗi lần nạp thất bại.</p>
 *
 * <p>Tắt bằng {@code DEFAULT_ACCOUNT_ENABLED=false} nếu sau này site có nhiều người dùng thật.</p>
 */
@Component
public class DefaultAccountFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(DefaultAccountFilter.class);

    /** Chỉ những nhánh mà trang "Câu của tôi" gọi; các API còn lại giữ nguyên quy tắc bảo mật. */
    static final String[] PREFIXES = {
            "/api/v1/me/words",
            "/api/v1/me/sentences",
            "/api/v1/ai/",
            "/api/v1/dictionary/"
    };

    /** Giữ tài khoản mặc định trong bộ nhớ chừng này rồi mới hỏi lại CSDL (để đổi mật khẩu / khoá có tác dụng). */
    static final long CACHE_TTL_MILLIS = 60_000L;

    private final AppProperties props;
    private final CustomUserDetailsService userDetailsService;

    /** Bản ghi đã nạp cùng thời điểm nạp; {@code volatile} vì nhiều luồng cùng đọc/ghi. */
    private volatile Cached cached;

    private record Cached(UserDetails user, long loadedAt) {
    }

    public DefaultAccountFilter(AppProperties props, CustomUserDetailsService userDetailsService) {
        this.props = props;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (props.defaultAccount() == null || !props.defaultAccount().enabled()) {
            return true;
        }
        return !matches(pathOf(request));
    }

    /** Đường dẫn có thuộc phần "Câu của tôi" hay không (tách riêng để kiểm thử). */
    static boolean matches(String path) {
        if (path == null) {
            return false;
        }
        for (String prefix : PREFIXES) {
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
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = defaultUser();
            if (user != null) {
                UsernamePasswordAuthenticationToken authentication =
                        UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }

    /** Tài khoản mặc định (có cache); {@code null} nếu không nạp được hoặc đang bị khoá. */
    private UserDetails defaultUser() {
        long now = System.currentTimeMillis();
        Cached local = cached;
        if (local != null && now - local.loadedAt() < CACHE_TTL_MILLIS) {
            return local.user();
        }
        UserDetails loaded = null;
        String username = props.defaultAccount().username();
        try {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (user.isEnabled() && user.isAccountNonLocked()) {
                loaded = user;
            } else {
                log.warn("Tài khoản mặc định '{}' đang bị khoá — request ẩn danh sẽ nhận 401", username);
            }
        } catch (UsernameNotFoundException e) {
            log.warn("Không tìm thấy tài khoản mặc định '{}' (app.default-account.username)", username);
        } catch (RuntimeException e) {
            log.warn("Không nạp được tài khoản mặc định '{}': {}", username, e.getMessage());
        }
        // Kể cả thất bại cũng ghi nhớ (user = null) để không dội CSDL mỗi request khi cấu hình sai.
        cached = new Cached(loaded, now);
        return loaded;
    }

    /** Đường dẫn của request đã bỏ context path (ứng dụng thường chạy ở context rỗng). */
    private static String pathOf(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }
}
