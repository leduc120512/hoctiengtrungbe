package com.example.bewebtiengtrung.security;

import com.example.bewebtiengtrung.config.AppProperties;
import com.example.bewebtiengtrung.module.user.entity.UserStatus;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link DefaultAccountFilter}: request ẩn danh tới phần "Câu của tôi" được chạy dưới tài khoản mặc định;
 * mọi nhánh khác và mọi request đã có danh tính đều không bị đụng tới.
 */
@ExtendWith(MockitoExtension.class)
class DefaultAccountFilterTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    private DefaultAccountFilter filter;

    private final CustomUserDetails owner = new CustomUserDetails(1L, "2222@hoctiengtrung.vn", "2222", "Tôi",
            "x", UserStatus.ACTIVE, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

    @BeforeEach
    void setUp() {
        AppProperties props = new AppProperties(null, null, new AppProperties.DefaultAccount(true, "2222"));
        filter = new DefaultAccountFilter(props, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Chỉ các nhánh của phần Câu của tôi mới được điền tài khoản mặc định")
    void chi_nhanh_cau_cua_toi() {
        assertThat(DefaultAccountFilter.matches("/api/v1/me/words")).isTrue();
        assertThat(DefaultAccountFilter.matches("/api/v1/me/words/import/preview")).isTrue();
        assertThat(DefaultAccountFilter.matches("/api/v1/me/sentences/generate")).isTrue();
        assertThat(DefaultAccountFilter.matches("/api/v1/ai/status")).isTrue();
        assertThat(DefaultAccountFilter.matches("/api/v1/dictionary/lookup")).isTrue();

        assertThat(DefaultAccountFilter.matches("/api/v1/admin/words")).isFalse();
        assertThat(DefaultAccountFilter.matches("/api/v1/me/srs/due")).isFalse();
        assertThat(DefaultAccountFilter.matches("/api/v1/auth/logout")).isFalse();
        assertThat(DefaultAccountFilter.matches(null)).isFalse();
    }

    @Test
    @DisplayName("Request ẩn danh ⇒ SecurityContext mang tài khoản mặc định; lần sau lấy từ cache, không hỏi lại CSDL")
    void an_danh_thi_dien_tai_khoan_mac_dinh() throws Exception {
        when(userDetailsService.loadUserByUsername("2222")).thenReturn(owner);

        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/me/words"), new MockHttpServletResponse(),
                mock(FilterChain.class));
        Authentication first = SecurityContextHolder.getContext().getAuthentication();
        assertThat(first).isNotNull();
        assertThat(first.getPrincipal()).isSameAs(owner);
        assertThat(SecurityUtils.currentUserId()).isEqualTo(1L);

        SecurityContextHolder.clearContext();
        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/me/sentences"), new MockHttpServletResponse(),
                mock(FilterChain.class));
        assertThat(SecurityUtils.currentUserId()).isEqualTo(1L);
        verify(userDetailsService, times(1)).loadUserByUsername("2222");
    }

    @Test
    @DisplayName("Đã có danh tính (token hợp lệ) thì giữ nguyên, không ghi đè")
    void co_token_thi_giu_nguyen() throws Exception {
        CustomUserDetails guest = new CustomUserDetails(2L, "1111@hoctiengtrung.vn", "1111", "Khách",
                "x", UserStatus.ACTIVE, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(guest, null, guest.getAuthorities()));

        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/me/words"), new MockHttpServletResponse(),
                mock(FilterChain.class));

        assertThat(SecurityUtils.currentUserId()).isEqualTo(2L);
        verify(userDetailsService, never()).loadUserByUsername("2222");
    }

    @Test
    @DisplayName("Nhánh khác hoặc tắt bằng cấu hình thì bộ lọc không chạy")
    void nhanh_khac_hoac_tat() throws Exception {
        assertThat(filter.shouldNotFilter(new MockHttpServletRequest("GET", "/api/v1/admin/words"))).isTrue();
        assertThat(filter.shouldNotFilter(new MockHttpServletRequest("GET", "/api/v1/me/words"))).isFalse();

        AppProperties off = new AppProperties(null, null, new AppProperties.DefaultAccount(false, "2222"));
        DefaultAccountFilter disabled = new DefaultAccountFilter(off, userDetailsService);
        assertThat(disabled.shouldNotFilter(new MockHttpServletRequest("GET", "/api/v1/me/words"))).isTrue();
    }

    @Test
    @DisplayName("Tài khoản mặc định không tồn tại ⇒ request đi tiếp như khách vãng lai, không văng lỗi")
    void khong_tim_thay_tai_khoan() throws Exception {
        when(userDetailsService.loadUserByUsername("2222")).thenThrow(new UsernameNotFoundException("x"));
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/me/words"), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
