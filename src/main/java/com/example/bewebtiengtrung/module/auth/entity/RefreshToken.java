package com.example.bewebtiengtrung.module.auth.entity;

import com.example.bewebtiengtrung.module.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Refresh token cua mot phien dang nhap.
 *
 * <p>Bang {@code refresh_tokens} CHI co cot {@code created_at}, KHONG co {@code updated_at},
 * vi vay entity nay KHONG duoc ke thua {@code BaseEntity}: no tu khai bao khoa chinh
 * va tu gan {@code createdAt} trong code.</p>
 *
 * <p>Vi ly do bao mat, chi luu SHA-256 (hex, 64 ky tu) cua token; token goc chi duoc
 * tra ve cho client dung mot lan tai thoi diem phat hanh.</p>
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Chu so huu token. LAZY theo quy uoc chung cua du an. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** SHA-256 dang hex cua token goc - dung 64 ky tu, unique trong DB. */
    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    /** Thoi diem het han (UTC). */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** Thoi diem bi thu hoi; null nghia la token con hieu luc. */
    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /** Bang khong co updated_at nen truong nay duoc gan thu cong trong service. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Chot an toan: neu service quen gan createdAt thi van co gia tri hop le. */
    @PrePersist
    void onPrePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    /** Token da bi thu hoi hay chua. */
    public boolean isRevoked() {
        return this.revokedAt != null;
    }

    /** Token da het han tai thoi diem {@code now} hay chua. */
    public boolean isExpired(Instant now) {
        return this.expiresAt == null || this.expiresAt.isBefore(now);
    }

    /** Token con dung duoc: chua thu hoi va chua het han. */
    public boolean isActive(Instant now) {
        return !isRevoked() && !isExpired(now);
    }
}
