package com.example.bewebtiengtrung.security;

import com.example.bewebtiengtrung.module.user.entity.User;
import com.example.bewebtiengtrung.module.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Nạp thông tin người dùng cho Spring Security.
 *
 * <p>Định danh đăng nhập là EMAIL (không phân biệt hoa thường).
 * Vai trò trong CSDL đã có dạng {@code ROLE_XXX} nên được giữ nguyên khi chuyển
 * thành {@code GrantedAuthority}.</p>
 *
 * <p>Tài khoản không ở trạng thái {@code ACTIVE} vẫn được nạp nhưng bị đánh dấu
 * disabled / locked, để Spring Security trả về lỗi xác thực phù hợp.</p>
 */
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @param email email đăng nhập
     * @throws UsernameNotFoundException khi không tồn tại tài khoản với email này
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("Email đăng nhập không được để trống");
        }
        // Chấp nhận cả email lẫn tên đăng nhập (ví dụ "2222"), để người dùng không phải nhớ email.
        String login = email.trim();
        User user = userRepository.findByEmailIgnoreCase(login)
                .or(() -> userRepository.findByUsernameIgnoreCase(login))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng: " + login));
        return CustomUserDetails.from(user);
    }

    /**
     * Nạp người dùng theo khoá chính — dùng khi access token chỉ còn claim {@code sub}.
     *
     * @param userId khoá chính của người dùng
     * @throws UsernameNotFoundException khi không tồn tại tài khoản với id này
     */
    public UserDetails loadUserById(Long userId) {
        if (userId == null) {
            throw new UsernameNotFoundException("Thiếu định danh người dùng trong token");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng với id: " + userId));
        return CustomUserDetails.from(user);
    }
}
