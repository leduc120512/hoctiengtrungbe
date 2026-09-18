package com.example.bewebtiengtrung.security;

import com.example.bewebtiengtrung.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Sinh và kiểm tra JSON Web Token (thuật toán HS256).
 *
 * <p>Cấu trúc access token:</p>
 * <ul>
 *   <li>{@code sub} — id người dùng (dạng chuỗi)</li>
 *   <li>{@code email} — email đăng nhập</li>
 *   <li>{@code roles} — danh sách tên vai trò {@code ROLE_XXX}</li>
 *   <li>{@code iss}, {@code iat}, {@code exp} — đơn vị phát hành và thời hạn</li>
 * </ul>
 *
 * <p>Refresh token KHÔNG phải JWT: đó là chuỗi ngẫu nhiên, hệ thống chỉ lưu bản băm
 * SHA-256 của nó trong bảng {@code refresh_tokens} (cột {@code token_hash} VARCHAR(64)).</p>
 *
 * <p>Khoá bí mật được kiểm tra ngay khi khởi tạo bean: nếu ngắn hơn 32 byte,
 * ứng dụng dừng khởi động (fail-fast) thay vì chạy với mức bảo mật yếu.</p>
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    /** Độ dài tối thiểu của khoá HS256 theo RFC 7518. */
    private static final int MIN_SECRET_BYTES = 32;

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLES = "roles";

    private final SecretKey secretKey;
    private final long accessTokenTtlSeconds;
    private final long refreshTokenTtlSeconds;
    private final String issuer;

    public JwtService(AppProperties properties) {
        AppProperties.Jwt jwtProperties = properties.jwt();
        if (jwtProperties == null) {
            throw new IllegalStateException("Thiếu cấu hình app.jwt trong application.yml");
        }

        String secret = jwtProperties.secret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Thiếu cấu hình app.jwt.secret (biến môi trường JWT_SECRET)");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "app.jwt.secret phải dài tối thiểu " + MIN_SECRET_BYTES + " byte để dùng cho HS256, "
                            + "hiện chỉ có " + secretBytes.length + " byte. "
                            + "Hãy đặt biến môi trường JWT_SECRET với chuỗi dài hơn.");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        this.accessTokenTtlSeconds = jwtProperties.accessTokenTtl() > 0 ? jwtProperties.accessTokenTtl() : 3600L;
        this.refreshTokenTtlSeconds = jwtProperties.refreshTokenTtl() > 0 ? jwtProperties.refreshTokenTtl() : 2592000L;
        this.issuer = jwtProperties.issuer() == null || jwtProperties.issuer().isBlank()
                ? "bewebtiengtrung"
                : jwtProperties.issuer();

        log.info("JwtService đã sẵn sàng: issuer={}, accessTokenTtl={}s, refreshTokenTtl={}s",
                this.issuer, this.accessTokenTtlSeconds, this.refreshTokenTtlSeconds);
    }

    // ------------------------------------------------------------------
    // Sinh access token
    // ------------------------------------------------------------------

    /** Sinh access token từ thông tin người dùng đã xác thực. */
    public String generateAccessToken(CustomUserDetails userDetails) {
        return generateAccessToken(userDetails.getId(), userDetails.getEmail(), userDetails.getRoleNames());
    }

    /** Sinh access token từ id, email và danh sách vai trò. */
    public String generateAccessToken(Long userId, String email, List<String> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTokenTtlSeconds);
        List<String> safeRoles = roles == null ? List.of() : new ArrayList<>(roles);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(issuer)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLES, safeRoles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /** Bí danh của {@link #generateAccessToken(CustomUserDetails)}. */
    public String generateToken(CustomUserDetails userDetails) {
        return generateAccessToken(userDetails);
    }

    /** Bí danh của {@link #generateAccessToken(Long, String, List)}. */
    public String generateToken(Long userId, String email, List<String> roles) {
        return generateAccessToken(userId, email, roles);
    }

    // ------------------------------------------------------------------
    // Đọc và kiểm tra access token
    // ------------------------------------------------------------------

    /**
     * Giải mã và kiểm tra chữ ký của access token.
     *
     * @throws JwtException khi token sai chữ ký, sai định dạng hoặc đã hết hạn
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Kiểm tra token còn hợp lệ hay không (không ném ngoại lệ ra ngoài). */
    public boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Access token không hợp lệ: {}", ex.getMessage());
            return false;
        }
    }

    /** Bí danh của {@link #isTokenValid(String)}. */
    public boolean validateToken(String token) {
        return isTokenValid(token);
    }

    /** Lấy id người dùng từ claim {@code sub}; trả về null nếu token hỏng. */
    public Long extractUserId(String token) {
        try {
            String subject = parseClaims(token).getSubject();
            return subject == null ? null : Long.valueOf(subject);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Không đọc được id người dùng từ token: {}", ex.getMessage());
            return null;
        }
    }

    /** Lấy email từ claim {@code email}; trả về null nếu token hỏng. */
    public String extractEmail(String token) {
        try {
            return parseClaims(token).get(CLAIM_EMAIL, String.class);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Không đọc được email từ token: {}", ex.getMessage());
            return null;
        }
    }

    /** Lấy danh sách vai trò từ claim {@code roles}; trả về danh sách rỗng nếu token hỏng. */
    public List<String> extractRoles(String token) {
        try {
            return rolesOf(parseClaims(token));
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Không đọc được vai trò từ token: {}", ex.getMessage());
            return List.of();
        }
    }

    /** Lấy danh sách vai trò từ claims đã giải mã sẵn. */
    public List<String> rolesOf(Claims claims) {
        Object raw = claims.get(CLAIM_ROLES);
        if (!(raw instanceof List<?> rawList)) {
            return List.of();
        }
        List<String> roles = new ArrayList<>(rawList.size());
        for (Object element : rawList) {
            if (element != null) {
                roles.add(String.valueOf(element));
            }
        }
        return roles;
    }

    // ------------------------------------------------------------------
    // Refresh token (chuỗi ngẫu nhiên + băm SHA-256)
    // ------------------------------------------------------------------

    /**
     * Sinh refresh token thô: chuỗi 64 ký tự hex sinh từ hai UUID ngẫu nhiên.
     * Chuỗi này chỉ trả về cho client MỘT lần, phía server chỉ lưu bản băm.
     */
    public String generateRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Băm SHA-256 và trả về chuỗi hex thường (đúng 64 ký tự,
     * khớp cột {@code refresh_tokens.token_hash} VARCHAR(64)).
     */
    public String sha256Hex(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Giá trị cần băm không được null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    builder.append('0');
                }
                builder.append(hex);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            // Không bao giờ xảy ra: SHA-256 là thuật toán bắt buộc của mọi JVM
            throw new IllegalStateException("JVM không hỗ trợ thuật toán SHA-256", ex);
        }
    }

    // ------------------------------------------------------------------
    // Thông tin cấu hình cho các module khác
    // ------------------------------------------------------------------

    /** Thời gian sống của access token (giây) — dùng cho trường {@code expiresIn}. */
    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    /** Bí danh của {@link #getAccessTokenTtlSeconds()}. */
    public long getAccessTokenTtl() {
        return accessTokenTtlSeconds;
    }

    /** Thời gian sống của refresh token (giây). */
    public long getRefreshTokenTtlSeconds() {
        return refreshTokenTtlSeconds;
    }

    /** Bí danh của {@link #getRefreshTokenTtlSeconds()}. */
    public long getRefreshTokenTtl() {
        return refreshTokenTtlSeconds;
    }

    /** Đơn vị phát hành token. */
    public String getIssuer() {
        return issuer;
    }

    /** Thời điểm hết hạn của một access token phát hành ngay bây giờ. */
    public Instant accessTokenExpiresAt() {
        return Instant.now().plusSeconds(accessTokenTtlSeconds);
    }

    /** Thời điểm hết hạn của một refresh token phát hành ngay bây giờ. */
    public Instant refreshTokenExpiresAt() {
        return Instant.now().plusSeconds(refreshTokenTtlSeconds);
    }
}
