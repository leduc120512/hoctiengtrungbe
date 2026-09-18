package com.example.bewebtiengtrung.security;

import com.example.bewebtiengtrung.common.exception.UnauthorizedException;
import com.example.bewebtiengtrung.module.user.entity.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Tiện ích tĩnh để lấy thông tin người dùng đang đăng nhập từ {@code SecurityContext}.
 *
 * <p>Dùng trong tầng service khi cần kiểm tra quyền sở hữu tài nguyên
 * (so sánh {@code currentUserId()} với chủ sở hữu, sai thì ném {@code ForbiddenException}).</p>
 */
public final class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Lớp tiện ích, không cho phép khởi tạo");
    }

    /**
     * Id của người dùng đang đăng nhập.
     *
     * @throws UnauthorizedException khi request chưa được xác thực
     */
    public static Long currentUserId() {
        return currentUserIdOrEmpty()
                .orElseThrow(() -> new UnauthorizedException("Bạn cần đăng nhập để sử dụng chức năng này."));
    }

    /** Id của người dùng đang đăng nhập, rỗng nếu là khách vãng lai. */
    public static Optional<Long> currentUserIdOrEmpty() {
        return currentUser().map(CustomUserDetails::getId);
    }

    /** Người dùng đang đăng nhập dưới dạng {@link CustomUserDetails}. */
    public static Optional<CustomUserDetails> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails);
        }
        // Principal dạng "anonymousUser" hoặc kiểu khác đều coi như chưa đăng nhập
        return Optional.empty();
    }

    /** Email của người dùng đang đăng nhập. */
    public static Optional<String> currentUserEmail() {
        return currentUser().map(CustomUserDetails::getEmail);
    }

    /** Đã đăng nhập hay chưa. */
    public static boolean isAuthenticated() {
        return currentUser().isPresent();
    }

    /** Người dùng hiện tại có vai trò {@code ROLE_ADMIN} hay không. */
    public static boolean isAdmin() {
        return hasRole(Role.ROLE_ADMIN);
    }

    /**
     * Kiểm tra người dùng hiện tại có một quyền cụ thể hay không.
     *
     * @param roleName tên quyền đầy đủ, ví dụ {@code ROLE_ADMIN}
     */
    public static boolean hasRole(String roleName) {
        if (roleName == null) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (roleName.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra quyền sở hữu: người dùng hiện tại phải là chủ tài nguyên, hoặc là quản trị viên.
     *
     * @param ownerId id chủ sở hữu tài nguyên (có thể null với tài nguyên hệ thống)
     */
    public static boolean isOwnerOrAdmin(Long ownerId) {
        if (isAdmin()) {
            return true;
        }
        return ownerId != null && currentUserIdOrEmpty().map(ownerId::equals).orElse(false);
    }
}
