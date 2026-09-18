package com.example.bewebtiengtrung.module.auth.service;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.module.auth.dto.AuthResponse;
import com.example.bewebtiengtrung.module.auth.dto.ChangePasswordRequest;
import com.example.bewebtiengtrung.module.auth.dto.LoginRequest;
import com.example.bewebtiengtrung.module.auth.dto.RefreshRequest;
import com.example.bewebtiengtrung.module.auth.dto.RegisterRequest;
import com.example.bewebtiengtrung.module.auth.dto.UpdateProfileRequest;
import com.example.bewebtiengtrung.module.auth.dto.UserResponse;

/**
 * Nghiep vu xac thuc va quan ly ho so ca nhan.
 * Controller chi goi qua interface nay, khong dung truc tiep repository.
 */
public interface AuthService {

    /**
     * Dang ky tai khoan moi va dang nhap ngay.
     *
     * @param request   thong tin dang ky
     * @param userAgent User-Agent cua client (co the null)
     * @param ipAddress dia chi IP cua client (co the null)
     */
    AuthResponse register(RegisterRequest request, String userAgent, String ipAddress);

    /** Dang nhap bang email + mat khau, cap nhat lastLoginAt va phat hanh cap token. */
    AuthResponse login(LoginRequest request, String userAgent, String ipAddress);

    /**
     * Lam moi token co xoay vong (rotation): thu hoi refresh token cu va phat hanh cap token moi.
     */
    AuthResponse refresh(RefreshRequest request, String userAgent, String ipAddress);

    /**
     * Dang xuat thiet bi hien tai: thu hoi dung refresh token duoc gui len.
     *
     * <p>Quyen thao tac duoc chung minh bang chinh viec so huu refresh token GOC
     * (chi chu phien moi giu duoc chuoi nay). Neu request co kem access token hop le thi
     * kiem tra them quyen so huu: token phai thuoc ve dung nguoi dung do.</p>
     *
     * @param authorizationHeader gia tri header {@code Authorization} (co the null)
     */
    MessageResponse logout(RefreshRequest request, String authorizationHeader);

    /**
     * Dang xuat khoi moi thiet bi: thu hoi toan bo refresh token cua nguoi dung.
     *
     * @param authorizationHeader gia tri header {@code Authorization} (co the null)
     */
    MessageResponse logoutAll(String authorizationHeader);

    /** Thong tin nguoi dung dang dang nhap. */
    UserResponse getCurrentUser();

    /** Cap nhat ho so ca nhan cua nguoi dung dang dang nhap. */
    UserResponse updateProfile(UpdateProfileRequest request);

    /** Doi mat khau, sau do thu hoi toan bo refresh token de bat dang nhap lai. */
    MessageResponse changePassword(ChangePasswordRequest request);
}
