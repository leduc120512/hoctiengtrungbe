package com.example.bewebtiengtrung.security;

import com.example.bewebtiengtrung.module.user.entity.Role;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.user.entity.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Đối tượng người dùng đã xác thực nằm trong {@code SecurityContext}.
 *
 * <p>Định danh đăng nhập là EMAIL, vì vậy {@link #getUsername()} trả về email
 * chứ không phải cột {@code users.username}.</p>
 *
 * <p>Tên vai trò trong CSDL đã ở dạng {@code ROLE_XXX} nên được đưa thẳng vào
 * {@link SimpleGrantedAuthority} mà KHÔNG thêm tiền tố {@code ROLE_} lần nữa.</p>
 */
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String accountUsername;
    private final String displayName;
    private final String password;
    private final UserStatus status;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id,
                             String email,
                             String accountUsername,
                             String displayName,
                             String password,
                             UserStatus status,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.accountUsername = accountUsername;
        this.displayName = displayName;
        this.password = password;
        this.status = status == null ? UserStatus.ACTIVE : status;
        this.authorities = authorities == null
                ? Collections.<GrantedAuthority>emptyList()
                : new ArrayList<GrantedAuthority>(authorities);
    }

    /** Dựng {@code UserDetails} từ entity {@link User} (roles đã được nạp EAGER). */
    public static CustomUserDetails from(User user) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (Role role : user.getRoles()) {
            if (role != null && role.getName() != null && !role.getName().isBlank()) {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            }
        }
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getPasswordHash(),
                user.getStatus(),
                authorities
        );
    }

    /** Khoá chính của người dùng. */
    public Long getId() {
        return id;
    }

    /** Email — đồng thời là định danh đăng nhập. */
    public String getEmail() {
        return email;
    }

    /** Cột {@code users.username} (tên hiển thị công khai), khác với {@link #getUsername()}. */
    public String getAccountUsername() {
        return accountUsername;
    }

    /** Tên hiển thị của người dùng. */
    public String getDisplayName() {
        return displayName;
    }

    /** Trạng thái tài khoản. */
    public UserStatus getStatus() {
        return status;
    }

    /** Danh sách tên vai trò dạng {@code ROLE_XXX} — tiện khi sinh JWT. */
    public List<String> getRoleNames() {
        return authorities.stream().map(GrantedAuthority::getAuthority).toList();
    }

    /** Người dùng có phải quản trị viên hay không. */
    public boolean isAdmin() {
        return authorities.stream()
                .anyMatch(authority -> Role.ROLE_ADMIN.equals(authority.getAuthority()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /** Định danh đăng nhập của Spring Security chính là email. */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** Tài khoản bị {@link UserStatus#LOCKED} coi như đang bị khoá. */
    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Chỉ tài khoản {@link UserStatus#ACTIVE} mới được phép sử dụng hệ thống. */
    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "CustomUserDetails{id=" + id + ", email='" + email + "'}";
    }
}
