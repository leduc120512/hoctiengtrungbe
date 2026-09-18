package com.example.bewebtiengtrung.module.auth.service;

import com.example.bewebtiengtrung.common.dto.MessageResponse;
import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.common.exception.BadRequestException;
import com.example.bewebtiengtrung.common.exception.ConflictException;
import com.example.bewebtiengtrung.common.exception.ForbiddenException;
import com.example.bewebtiengtrung.common.exception.NotFoundException;
import com.example.bewebtiengtrung.common.exception.UnauthorizedException;
import com.example.bewebtiengtrung.module.auth.dto.AuthResponse;
import com.example.bewebtiengtrung.module.auth.dto.ChangePasswordRequest;
import com.example.bewebtiengtrung.module.auth.dto.LoginRequest;
import com.example.bewebtiengtrung.module.auth.dto.RefreshRequest;
import com.example.bewebtiengtrung.module.auth.dto.RegisterRequest;
import com.example.bewebtiengtrung.module.auth.dto.UpdateProfileRequest;
import com.example.bewebtiengtrung.module.auth.dto.UserResponse;
import com.example.bewebtiengtrung.module.auth.entity.RefreshToken;
import com.example.bewebtiengtrung.module.auth.mapper.UserMapper;
import com.example.bewebtiengtrung.module.auth.repository.RefreshTokenRepository;
import com.example.bewebtiengtrung.module.user.entity.Role;
import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.user.entity.UserStatus;
import com.example.bewebtiengtrung.module.user.repository.RoleRepository;
import com.example.bewebtiengtrung.module.user.repository.UserRepository;
import com.example.bewebtiengtrung.security.JwtService;
import com.example.bewebtiengtrung.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Trien khai nghiep vu xac thuc.
 *
 * <p>Nguyen tac bao mat:</p>
 * <ul>
 *   <li>Mat khau luu bang BCrypt thong qua {@code PasswordEncoder}.</li>
 *   <li>Refresh token la chuoi ngau nhien, DB chi luu SHA-256 hex 64 ky tu cua no.</li>
 *   <li>Moi lan refresh deu xoay vong token: thu hoi ban ghi cu, ghi ban ghi moi.</li>
 * </ul>
 *
 * <p>Viec sinh / bam token duoc uy quyen hoan toan cho {@code JwtService} de access token
 * phat hanh o day khop chinh xac dinh dang ma {@code JwtAuthenticationFilter} doc lai.</p>
 */
@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    /** Vai tro mac dinh gan cho tai khoan moi dang ky. */
    private static final String DEFAULT_ROLE_NAME = Role.ROLE_USER;
    private static final String DEFAULT_NATIVE_LANGUAGE = "vi";
    private static final int DEFAULT_HSK_LEVEL = 1;
    private static final int MAX_USER_AGENT_LENGTH = 255;
    private static final int MAX_IP_LENGTH = 45;
    /** Tien to chuan cua header Authorization. */
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    // ------------------------------------------------------------------
    // Dang ky / dang nhap
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, String userAgent, String ipAddress) {
        String email = normalizeEmail(request.email());
        String username = request.username().trim();

        // Kiem tra trung lap truoc khi ghi de tra ve 409 ro rang thay vi loi rang buoc cua DB.
        // Phai dung ...IgnoreCase cho ca hai: bang users dung collation utf8mb4_0900_ai_ci nen
        // uk_users_username coi "hocvien01" va "HocVien01" la trung nhau.
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email da duoc su dung");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("Ten dang nhap da duoc su dung");
        }

        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_NOT_CONFIGURED",
                        "He thong chua duoc cau hinh vai tro " + DEFAULT_ROLE_NAME));

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName().trim());
        user.setAvatarUrl(null);
        user.setNativeLanguage(DEFAULT_NATIVE_LANGUAGE);
        user.setCurrentHskLevel(DEFAULT_HSK_LEVEL);
        user.setStatus(UserStatus.ACTIVE);
        user.addRole(defaultRole);

        User saved = userRepository.save(user);
        return issueTokens(saved, userAgent, ipAddress);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String userAgent, String ipAddress) {
        String email = normalizeEmail(request.email());

        // Uy quyen kiem tra thong tin dang nhap cho AuthenticationManager (DaoAuthenticationProvider)
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(email, request.password()));
        } catch (DisabledException | LockedException ex) {
            throw new ForbiddenException("Tai khoan da bi khoa hoac vo hieu hoa");
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Email hoac mat khau khong dung");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Email hoac mat khau khong dung"));

        user.setLastLoginAt(Instant.now());
        User saved = userRepository.save(user);
        return issueTokens(saved, userAgent, ipAddress);
    }

    // ------------------------------------------------------------------
    // Lam moi token co xoay vong (rotation)
    // ------------------------------------------------------------------

    /**
     * {@code noRollbackFor}: khi phat hien mot token da thu hoi bi dung lai, ta thu hoi ca phien
     * roi nem UnauthorizedException. Neu de transaction rollback thi thao tac thu hoi day cung
     * bi huy, mat luon tac dung phong ve.
     */
    @Override
    @Transactional(noRollbackFor = UnauthorizedException.class)
    public AuthResponse refresh(RefreshRequest request, String userAgent, String ipAddress) {
        Instant now = Instant.now();
        String tokenHash = jwtService.sha256Hex(request.refreshToken());

        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token khong hop le"));

        if (stored.isRevoked()) {
            // Token da thu hoi ma van bi dung lai: nghi ngo bi danh cap, thu hoi toan bo phien
            refreshTokenRepository.revokeAllByUserId(stored.getUser().getId(), now);
            throw new UnauthorizedException("Refresh token da bi thu hoi");
        }
        if (stored.isExpired(now)) {
            throw new UnauthorizedException("Refresh token da het han");
        }

        User user = stored.getUser();
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException("Tai khoan da bi khoa hoac vo hieu hoa");
        }

        // Xoay vong: thu hoi ban ghi cu roi phat hanh cap token moi
        stored.setRevokedAt(now);
        refreshTokenRepository.save(stored);

        return issueTokens(user, userAgent, ipAddress);
    }

    // ------------------------------------------------------------------
    // Dang xuat
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public MessageResponse logout(RefreshRequest request, String authorizationHeader) {
        // Nguoi goi da chung minh quyen bang chinh refresh token goc, nen KHONG bat buoc
        // phai co access token. Neu co thi dung de kiem tra quyen so huu.
        Long callerId = resolveCallerId(authorizationHeader);
        String tokenHash = jwtService.sha256Hex(request.refreshToken());

        Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash(tokenHash);
        if (found.isPresent()) {
            RefreshToken token = found.get();
            if (callerId != null && !callerId.equals(token.getUser().getId())) {
                throw new ForbiddenException("Refresh token khong thuoc ve nguoi dung hien tai");
            }
            if (!token.isRevoked()) {
                token.setRevokedAt(Instant.now());
                refreshTokenRepository.save(token);
            }
        }
        // Idempotent: token khong ton tai van coi nhu da dang xuat
        return new MessageResponse("Dang xuat thanh cong");
    }

    @Override
    @Transactional
    public MessageResponse logoutAll(String authorizationHeader) {
        Long callerId = resolveCallerId(authorizationHeader);
        if (callerId == null) {
            throw new UnauthorizedException("Ban can dang nhap de su dung chuc nang nay");
        }
        refreshTokenRepository.revokeAllByUserId(callerId, Instant.now());
        return new MessageResponse("Da dang xuat khoi tat ca thiet bi");
    }

    // ------------------------------------------------------------------
    // Ho so ca nhan
    // ------------------------------------------------------------------

    @Override
    public UserResponse getCurrentUser() {
        return userMapper.toResponse(requireCurrentUser());
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = requireCurrentUser();

        user.setDisplayName(request.displayName().trim());
        user.setAvatarUrl(trimToNull(request.avatarUrl()));

        String nativeLanguage = trimToNull(request.nativeLanguage());
        if (nativeLanguage != null) {
            user.setNativeLanguage(nativeLanguage);
        }
        if (request.currentHskLevel() != null) {
            user.setCurrentHskLevel(request.currentHskLevel());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public MessageResponse changePassword(ChangePasswordRequest request) {
        User user = requireCurrentUser();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mat khau hien tai khong dung");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mat khau moi phai khac mat khau hien tai");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Doi mat khau thi vo hieu hoa moi phien cu, bat buoc dang nhap lai
        refreshTokenRepository.revokeAllByUserId(user.getId(), Instant.now());
        return new MessageResponse("Doi mat khau thanh cong. Vui long dang nhap lai.");
    }

    // ------------------------------------------------------------------
    // Ham ho tro noi bo
    // ------------------------------------------------------------------

    /**
     * Xac dinh id nguoi goi cho hai endpoint dang xuat, tra ve null neu khong xac dinh duoc.
     *
     * <p>Nhanh {@code /api/v1/auth/**} duoc {@code JwtAuthenticationFilter} bo qua hoan toan
     * ({@code shouldNotFilter}), nen o day {@code SecurityContext} luon rong du client co gui
     * {@code Authorization: Bearer ...}. Vi vay phai tu doc access token tu header, va chi tin
     * {@code sub} sau khi {@code JwtService} da kiem tra chu ky + han su dung.</p>
     *
     * <p>Van uu tien {@code SecurityContext} truoc de neu sau nay bo loc chay tren nhanh auth
     * thi luong nay tu dong dung ket qua chinh thong.</p>
     */
    private Long resolveCallerId(String authorizationHeader) {
        Optional<Long> fromContext = SecurityUtils.currentUserIdOrEmpty();
        if (fromContext.isPresent()) {
            return fromContext.get();
        }
        String accessToken = bearerTokenOf(authorizationHeader);
        if (accessToken == null || !jwtService.isTokenValid(accessToken)) {
            return null;
        }
        return jwtService.extractUserId(accessToken);
    }

    /** Tach phan token phia sau tien to {@code Bearer } trong header Authorization. */
    private static String bearerTokenOf(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return trimToNull(authorizationHeader.substring(BEARER_PREFIX.length()));
    }

    /** Lay user dang dang nhap tu SecurityContext. */
    private User requireCurrentUser() {
        Long userId = SecurityUtils.currentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay nguoi dung"));
    }

    /**
     * Phat hanh access token + refresh token moi va luu SHA-256 cua refresh token vao DB.
     * Chuoi refresh token GOC chi duoc tra ve cho client dung mot lan tai day.
     */
    private AuthResponse issueTokens(User user, String userAgent, String ipAddress) {
        List<String> roles = userMapper.roleNames(user);
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roles);

        String rawRefreshToken = jwtService.generateRefreshToken();
        Instant now = Instant.now();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(jwtService.sha256Hex(rawRefreshToken));
        refreshToken.setCreatedAt(now);
        refreshToken.setExpiresAt(now.plusSeconds(jwtService.getRefreshTokenTtlSeconds()));
        refreshToken.setUserAgent(truncate(userAgent, MAX_USER_AGENT_LENGTH));
        refreshToken.setIpAddress(truncate(ipAddress, MAX_IP_LENGTH));
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.of(accessToken, rawRefreshToken,
                jwtService.getAccessTokenTtlSeconds(), userMapper.toResponse(user));
    }

    /** Chuan hoa email: bo khoang trang thua va ha ve chu thuong. */
    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    /** Chuoi rong hoac chi gom khoang trang se tro thanh null. */
    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /** Cat bot chuoi cho vua do dai cot trong DB. */
    private static String truncate(String value, int maxLength) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }
}
