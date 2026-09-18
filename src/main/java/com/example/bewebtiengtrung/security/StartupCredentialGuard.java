package com.example.bewebtiengtrung.security;

import com.example.bewebtiengtrung.module.auth.repository.RefreshTokenRepository;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.user.entity.UserStatus;
import com.example.bewebtiengtrung.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Bảo vệ tài khoản mặc định khi deploy công khai.
 *
 * <p>Seed V2 tạo sẵn {@code admin@hoctiengtrung.vn / Admin@123} và {@code demo@hoctiengtrung.vn / Demo@123}
 * — hai mật khẩu này nằm trong README công khai, nên trên máy chủ thật KHÔNG được để nguyên.
 * Lúc khởi động, lớp này:
 * <ol>
 *   <li>nếu có biến môi trường {@code ADMIN_PASSWORD} ⇒ đặt lại mật khẩu admin theo biến đó (chỉ khi
 *       khác mật khẩu hiện tại) và thu hồi mọi refresh token cũ của admin;</li>
 *   <li>nếu không có biến và admin vẫn dùng {@code Admin@123} ⇒ ghi cảnh báo WARN thật rõ;</li>
 *   <li>nếu {@code DEMO_ACCOUNT_ENABLED=false} ⇒ khoá tài khoản demo (status LOCKED); {@code true} ⇒ mở lại.</li>
 * </ol>
 * Không bao giờ log mật khẩu.
 */
@Component
public class StartupCredentialGuard implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupCredentialGuard.class);
    static final String ADMIN_EMAIL = "admin@hoctiengtrung.vn";
    static final String DEMO_EMAIL = "demo@hoctiengtrung.vn";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@123";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String demoEnabled;

    public StartupCredentialGuard(UserRepository userRepository,
                                  RefreshTokenRepository refreshTokenRepository,
                                  PasswordEncoder passwordEncoder,
                                  @Value("${ADMIN_PASSWORD:}") String adminPassword,
                                  @Value("${DEMO_ACCOUNT_ENABLED:}") String demoEnabled) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword == null ? "" : adminPassword.trim();
        this.demoEnabled = demoEnabled == null ? "" : demoEnabled.trim().toLowerCase();
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        userRepository.findByEmailIgnoreCase(ADMIN_EMAIL).ifPresent(this::guardAdmin);
        userRepository.findByEmailIgnoreCase(DEMO_EMAIL).ifPresent(this::guardDemo);
    }

    private void guardAdmin(User admin) {
        if (!adminPassword.isEmpty()) {
            if (adminPassword.length() < 8) {
                log.error("ADMIN_PASSWORD quá ngắn (< 8 ký tự) — bỏ qua, mật khẩu admin KHÔNG được đổi");
                return;
            }
            if (passwordEncoder.matches(adminPassword, admin.getPasswordHash())) {
                return; // đã đúng mật khẩu mong muốn, không làm gì
            }
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            userRepository.save(admin);
            refreshTokenRepository.revokeAllByUserId(admin.getId(), Instant.now());
            log.info("Đã đặt lại mật khẩu admin theo biến môi trường ADMIN_PASSWORD và thu hồi refresh token cũ");
            return;
        }
        if (passwordEncoder.matches(DEFAULT_ADMIN_PASSWORD, admin.getPasswordHash())) {
            log.warn("=================================================================================");
            log.warn("  CẢNH BÁO BẢO MẬT: tài khoản admin đang dùng MẬT KHẨU MẶC ĐỊNH có trong README.");
            log.warn("  Đặt biến môi trường ADMIN_PASSWORD (>= 8 ký tự) rồi khởi động lại để đổi ngay.");
            log.warn("=================================================================================");
        }
    }

    private void guardDemo(User demo) {
        if (demoEnabled.isEmpty()) {
            return;
        }
        boolean enable = demoEnabled.equals("true") || demoEnabled.equals("1") || demoEnabled.equals("yes");
        UserStatus wanted = enable ? UserStatus.ACTIVE : UserStatus.LOCKED;
        if (demo.getStatus() != wanted) {
            demo.setStatus(wanted);
            userRepository.save(demo);
            if (!enable) {
                refreshTokenRepository.revokeAllByUserId(demo.getId(), Instant.now());
            }
            log.info("Tài khoản demo đã được {} theo DEMO_ACCOUNT_ENABLED={}", enable ? "mở" : "khoá", demoEnabled);
        }
    }
}
