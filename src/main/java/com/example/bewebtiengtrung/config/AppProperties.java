package com.example.bewebtiengtrung.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * Cấu hình riêng của ứng dụng, ánh xạ nhánh {@code app.*} trong {@code application.yml}.
 *
 * <p>Được đăng ký bằng {@code @EnableConfigurationProperties(AppProperties.class)}
 * trong {@link CorsConfig} (không sửa lớp {@code BewebtiengtrungApplication}).</p>
 *
 * <p>Mọi giá trị đều có thể ghi đè bằng biến môi trường, không hard-code bí mật trong mã nguồn.</p>
 *
 * @param jwt            cấu hình JSON Web Token
 * @param cors           cấu hình CORS cho frontend
 * @param defaultAccount tài khoản chạy các request ẩn danh của phần "Câu của tôi"
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @DefaultValue Jwt jwt,
        @DefaultValue Cors cors,
        @DefaultValue DefaultAccount defaultAccount
) {

    /**
     * Cấu hình JWT.
     *
     * @param secret          khoá bí mật HS256 — bắt buộc tối thiểu 32 byte
     * @param accessTokenTtl  thời gian sống của access token, tính bằng giây
     * @param refreshTokenTtl thời gian sống của refresh token, tính bằng giây
     * @param issuer          tên đơn vị phát hành ghi trong claim {@code iss}
     */
    public record Jwt(
            @DefaultValue("change-me-in-production-min-32-bytes-long-secret-key") String secret,
            @DefaultValue("3600") long accessTokenTtl,
            @DefaultValue("2592000") long refreshTokenTtl,
            @DefaultValue("bewebtiengtrung") String issuer
    ) {
    }

    /**
     * Cấu hình CORS.
     *
     * @param allowedOrigins danh sách origin của frontend được phép gọi API
     */
    public record Cors(
            @DefaultValue({"http://localhost:3000", "http://localhost:5173", "http://localhost:4200"})
            List<String> allowedOrigins
    ) {
    }

    /**
     * Tài khoản mặc định cho phần "Câu của tôi" (xem {@code security.DefaultAccountFilter}).
     *
     * <p>Site cá nhân, một người dùng: frontend không bắt đăng nhập nữa. Request không mang token tới
     * các API của phần này được chạy dưới tài khoản {@code username}; request có token vẫn dùng đúng
     * tài khoản trong token.</p>
     *
     * @param enabled  {@code false} để bắt đăng nhập trở lại
     * @param username tên đăng nhập (hoặc email) của tài khoản mặc định
     */
    public record DefaultAccount(
            @DefaultValue("true") boolean enabled,
            @DefaultValue("2222") String username
    ) {
    }
}
