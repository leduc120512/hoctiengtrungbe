package com.example.bewebtiengtrung.module.ai.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Đăng ký {@link AiProperties} (nhánh {@code app.ai.*}) — không sửa lớp khởi động.
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfig {
}
