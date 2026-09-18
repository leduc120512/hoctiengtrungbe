package com.example.bewebtiengtrung.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Bật cơ chế auditing của Spring Data JPA để tự động điền
 * {@code created_at} / {@code updated_at} cho các entity kế thừa {@code BaseEntity}.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
