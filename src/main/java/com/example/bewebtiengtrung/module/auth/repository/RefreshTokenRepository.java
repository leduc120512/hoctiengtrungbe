package com.example.bewebtiengtrung.module.auth.repository;

import com.example.bewebtiengtrung.module.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

/**
 * Truy van refresh token. Luon tra cuu bang HASH, khong bao gio bang token goc.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Tim token theo SHA-256 hex. Fetch san user de tranh N+1 khi kiem tra chu so huu.
     */
    @Query("select rt from RefreshToken rt join fetch rt.user where rt.tokenHash = :tokenHash")
    Optional<RefreshToken> findByTokenHash(@Param("tokenHash") String tokenHash);

    /** Xoa han moi token cua mot user (dung khi xoa tai khoan). */
    void deleteByUserId(Long userId);

    /**
     * Thu hoi (revoke) toan bo token dang con hieu luc cua mot user.
     * Dung khi doi mat khau hoac dang xuat khoi tat ca thiet bi.
     *
     * @return so ban ghi bi thu hoi
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshToken rt set rt.revokedAt = :now where rt.user.id = :userId and rt.revokedAt is null")
    int revokeAllByUserId(@Param("userId") Long userId, @Param("now") Instant now);

    /** Don dep cac token da het han truoc moc thoi gian chi dinh. */
    long deleteByExpiresAtBefore(Instant threshold);
}
