package com.example.bewebtiengtrung.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Cấu hình CORS cho frontend (React / Vue / Angular chạy ở localhost khi phát triển).
 *
 * <p>Bean {@link CorsConfigurationSource} ở đây được {@code SecurityConfig} sử dụng
 * thông qua {@code http.cors(Customizer.withDefaults())}.</p>
 *
 * <p>Lớp này cũng chịu trách nhiệm đăng ký {@link AppProperties} vì
 * lớp khởi động {@code BewebtiengtrungApplication} không được phép chỉnh sửa.</p>
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(AppProperties properties) {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = properties.cors() == null ? null : properties.cors().allowedOrigins();
        if (origins == null || origins.isEmpty()) {
            // Không cấu hình thì mặc định cho phép mọi origin nhưng tắt cookie để vẫn an toàn
            configuration.setAllowedOriginPatterns(List.of("*"));
            configuration.setAllowCredentials(false);
        } else {
            configuration.setAllowedOrigins(new ArrayList<>(origins));
            configuration.setAllowCredentials(true);
        }

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
