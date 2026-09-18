package com.example.bewebtiengtrung.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Cấu hình tài liệu OpenAPI / Swagger UI (truy cập tại {@code /swagger-ui.html}).
 *
 * <p>Khai báo sẵn scheme bảo mật {@code bearerAuth} và áp dụng cho toàn bộ API,
 * nhờ vậy nút "Authorize" trên Swagger UI cho phép dán access token JWT
 * và mọi request thử nghiệm sẽ tự kèm header {@code Authorization: Bearer <token>}.</p>
 */
@Configuration
public class OpenApiConfig {

    /** Tên scheme bảo mật dùng chung cho toàn bộ tài liệu API. */
    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openApi() {
        Info info = new Info()
                .title("API Học Tiếng Trung")
                .version("v1")
                .description("""
                        API cho website học tiếng Trung dành cho người Việt.

                        Các nhóm chức năng chính:
                        - Xác thực & tài khoản (đăng ký, đăng nhập, làm mới token, hồ sơ cá nhân)
                        - Từ vựng theo chủ đề và cấp độ HSK
                        - Khoá học, bài học, tiến độ học tập
                        - Bài kiểm tra trắc nghiệm và kết quả làm bài
                        - Flashcard ôn tập ngắt quãng theo thuật toán SM-2

                        Cách dùng: gọi `/api/v1/auth/login` để lấy `accessToken`,
                        sau đó bấm **Authorize** và dán token vào ô `bearerAuth`.
                        """)
                .contact(new Contact().name("Đội phát triển bewebtiengtrung"))
                .license(new License().name("Apache License 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"));

        SecurityScheme bearerScheme = new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .description("Dán access token JWT lấy được từ /api/v1/auth/login hoặc /api/v1/auth/refresh");

        return new OpenAPI()
                .info(info)
                .servers(List.of(new Server().url("/").description("Máy chủ hiện tại")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, bearerScheme));
    }
}
