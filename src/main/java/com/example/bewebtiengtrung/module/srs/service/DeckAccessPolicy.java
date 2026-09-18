package com.example.bewebtiengtrung.module.srs.service;

import com.example.bewebtiengtrung.common.exception.ForbiddenException;
import com.example.bewebtiengtrung.module.srs.entity.Deck;
import com.example.bewebtiengtrung.security.SecurityUtils;
import org.springframework.stereotype.Component;

/**
 * Quy tắc phân quyền trên bộ thẻ, dùng chung cho {@code DeckServiceImpl} và {@code SrsServiceImpl}.
 *
 * <ul>
 *   <li>Đọc: deck công khai, deck hệ thống, deck của chính mình (ADMIN đọc được tất cả).</li>
 *   <li>Ghi: chỉ chủ sở hữu hoặc ROLE_ADMIN.</li>
 * </ul>
 *
 * <p>Chỉ so sánh trên khoá ngoại {@code ownerId} nên không cần fetch join entity
 * {@code User} – tránh phát sinh truy vấn phụ khi kiểm tra quyền.</p>
 */
@Component
public class DeckAccessPolicy {

    /** Người dùng có phải chủ sở hữu deck hay không. */
    public boolean isOwner(Deck deck, Long userId) {
        return deck.getOwnerId() != null
                && userId != null
                && userId.equals(deck.getOwnerId());
    }

    /** Deck có được phép hiển thị cho người dùng hay không. */
    public boolean canRead(Deck deck, Long userId) {
        return Boolean.TRUE.equals(deck.getIsPublic())
                || Boolean.TRUE.equals(deck.getIsSystem())
                || isOwner(deck, userId)
                || SecurityUtils.isAdmin();
    }

    /** Deck có được phép chỉnh sửa hay không: chủ sở hữu hoặc quản trị viên. */
    public boolean canWrite(Deck deck, Long userId) {
        return isOwner(deck, userId) || SecurityUtils.isAdmin();
    }

    /** Ném {@link ForbiddenException} nếu người dùng không được xem deck. */
    public void requireRead(Deck deck, Long userId) {
        if (!canRead(deck, userId)) {
            throw new ForbiddenException("Bạn không có quyền xem bộ thẻ này");
        }
    }

    /** Ném {@link ForbiddenException} nếu người dùng không được sửa deck. */
    public void requireWrite(Deck deck, Long userId) {
        if (!canWrite(deck, userId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa bộ thẻ này");
        }
    }
}
