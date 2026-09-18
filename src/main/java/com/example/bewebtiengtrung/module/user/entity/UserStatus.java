package com.example.bewebtiengtrung.module.user.entity;

/**
 * Trạng thái tài khoản, ánh xạ cột {@code users.status} VARCHAR(20).
 *
 * <p>Chỉ {@link #ACTIVE} mới được phép đăng nhập; hai trạng thái còn lại
 * bị {@code CustomUserDetailsService} đánh dấu là khoá / vô hiệu hoá.</p>
 */
public enum UserStatus {

    /** Tài khoản hoạt động bình thường. */
    ACTIVE,

    /** Tài khoản bị khoá tạm thời (ví dụ nhập sai mật khẩu quá nhiều lần). */
    LOCKED,

    /** Tài khoản bị vô hiệu hoá / người dùng tự xoá. */
    DISABLED
}
