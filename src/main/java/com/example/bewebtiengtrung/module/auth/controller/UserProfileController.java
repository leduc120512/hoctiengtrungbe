package com.example.bewebtiengtrung.module.auth.controller;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.module.auth.dto.ChangePasswordRequest;
import com.example.bewebtiengtrung.module.auth.dto.UpdateProfileRequest;
import com.example.bewebtiengtrung.module.auth.dto.UserResponse;
import com.example.bewebtiengtrung.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API ho so ca nhan cua nguoi dung dang dang nhap.
 * Moi endpoint deu thao tac tren chinh tai khoan trong SecurityContext nen khong nhan userId tu client.
 */
@RestController
@RequestMapping("/api/v1/me")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Ho so ca nhan", description = "Xem va cap nhat thong tin tai khoan dang dang nhap")
public class UserProfileController {

    private final AuthService authService;

    public UserProfileController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Lay thong tin nguoi dung dang dang nhap")
    @GetMapping
    public UserResponse getCurrentUser() {
        return authService.getCurrentUser();
    }

    @Operation(summary = "Cap nhat ho so ca nhan")
    @PutMapping
    public UserResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return authService.updateProfile(request);
    }

    @Operation(summary = "Doi mat khau, moi phien dang nhap cu se bi thu hoi")
    @PostMapping("/change-password")
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return authService.changePassword(request);
    }
}
