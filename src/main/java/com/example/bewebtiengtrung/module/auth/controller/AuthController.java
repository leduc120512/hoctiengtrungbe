package com.example.bewebtiengtrung.module.auth.controller;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.module.auth.dto.AuthResponse;
import com.example.bewebtiengtrung.module.auth.dto.LoginRequest;
import com.example.bewebtiengtrung.module.auth.dto.RefreshRequest;
import com.example.bewebtiengtrung.module.auth.dto.RegisterRequest;
import com.example.bewebtiengtrung.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API xac thuc: dang ky, dang nhap, lam moi token va dang xuat.
 * Controller khong chua logic nghiep vu, chi dieu phoi sang {@link AuthService}.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Xac thuc", description = "Dang ky, dang nhap, lam moi token va dang xuat")
public class AuthController {

    /** Header pho bien khi ung dung chay sau proxy / load balancer. */
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Dang ky tai khoan moi va tra ve cap token")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request,
                                 @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
                                 HttpServletRequest httpRequest) {
        return authService.register(request, userAgent, resolveClientIp(httpRequest));
    }

    @Operation(summary = "Dang nhap bang email va mat khau")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request,
                              @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
                              HttpServletRequest httpRequest) {
        return authService.login(request, userAgent, resolveClientIp(httpRequest));
    }

    @Operation(summary = "Lam moi access token, refresh token cu se bi thu hoi (rotation)")
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request,
                                @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
                                HttpServletRequest httpRequest) {
        return authService.refresh(request, userAgent, resolveClientIp(httpRequest));
    }

    @Operation(summary = "Dang xuat thiet bi hien tai, thu hoi refresh token duoc gui len")
    @PostMapping("/logout")
    public MessageResponse logout(
            @Valid @RequestBody RefreshRequest request,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return authService.logout(request, authorization);
    }

    @Operation(summary = "Dang xuat khoi tat ca thiet bi, thu hoi moi refresh token")
    @PostMapping("/logout-all")
    public MessageResponse logoutAll(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return authService.logoutAll(authorization);
    }

    /**
     * Lay IP client: uu tien phan tu dau tien cua X-Forwarded-For khi chay sau proxy.
     */
    private static String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwardedFor = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            int separatorIndex = forwardedFor.indexOf(',');
            String firstHop = separatorIndex >= 0 ? forwardedFor.substring(0, separatorIndex) : forwardedFor;
            if (!firstHop.isBlank()) {
                return firstHop.trim();
            }
        }
        return request.getRemoteAddr();
    }
}
