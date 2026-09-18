package com.example.bewebtiengtrung.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Cấu hình bảo mật của toàn bộ API.
 *
 * <p>Nguyên tắc:</p>
 * <ul>
 *   <li>Hoàn toàn stateless — không session, không CSRF, xác thực bằng JWT;</li>
 *   <li>CORS lấy từ {@code app.cors.allowed-origins} (bean {@link CorsConfigurationSource});</li>
 *   <li>Các API đọc nội dung học tập (topics, words, courses, lessons, quizzes) mở công khai
 *       với phương thức GET; mọi thao tác ghi đều yêu cầu đăng nhập;</li>
 *   <li>Nhánh {@code /api/v1/admin/**} chỉ dành cho {@code ROLE_ADMIN};</li>
 *   <li>Phần "Câu của tôi" ({@code /api/v1/me/words/**}, {@code /api/v1/me/sentences/**}, {@code /api/v1/ai/**},
 *       {@code /api/v1/dictionary/**}) không bắt đăng nhập: request không mang token được
 *       {@link DefaultAccountFilter} chạy dưới tài khoản mặc định (site cá nhân một người dùng);</li>
 *   <li>Bật {@code @EnableMethodSecurity} để các module dùng được {@code @PreAuthorize}.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** Các đường dẫn công khai không phụ thuộc phương thức HTTP. */
    private static final String[] PUBLIC_PATHS = {
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/actuator/health",
            "/actuator/health/**",
            // Bộ lọc bảo mật chạy cả với dispatcher type ERROR: nếu không mở /error thì
            // mọi lỗi của request ẩn danh sẽ bị đổi thành 401 thay vì mã lỗi thật.
            "/error"
    };

    /** Các đường dẫn chỉ mở công khai với phương thức GET (nội dung học tập). */
    private static final String[] PUBLIC_GET_PATHS = {
            "/api/v1/topics/**",
            "/api/v1/words/**",
            "/api/v1/courses/**",
            "/api/v1/lessons/**",
            "/api/v1/quizzes/**"
    };

    /** Mã hoá mật khẩu bằng BCrypt (chuỗi băm 60 ký tự, vừa cột VARCHAR(100)). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Spring Security 7: UserDetailsService truyền qua constructor, không còn setter. */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /** AuthenticationManager để module auth gọi khi đăng nhập bằng email + mật khẩu. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   DefaultAccountFilter defaultAccountFilter,
                                                   RestAuthenticationEntryPoint authenticationEntryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight CORS luôn phải đi qua
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Khu vực quản trị phải đặt trước các quy tắc công khai
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_PATHS).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Sau bộ lọc JWT: chỉ điền tài khoản mặc định khi token vắng mặt hoặc không hợp lệ.
                .addFilterAfter(defaultAccountFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
