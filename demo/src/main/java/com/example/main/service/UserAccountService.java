package com.example.main.service;

import com.example.main.dto.request.UserAccountRequest;
import com.example.main.dto.response.UserAccountResponse;
import com.example.main.entity.UserEntity;
import com.example.main.enums.Role;
import com.example.main.repository.UserRepository;
import com.example.main.utils.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Xử lý nghiệp vụ tài khoản: đăng nhập và CRUD trên bảng users.
 */
@Service
public class UserAccountService {

    private final UserRepository userRepository;

    public UserAccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Xác thực đăng nhập. Trả về thông tin tài khoản nếu đúng, ngược lại Optional.empty().
     */
    @Transactional(readOnly = true)
    public Optional<UserAccountResponse> authenticate(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim())
                .filter(user -> PasswordHasher.matches(rawPassword, user.getPasswordHash()))
                .map(UserEntity::toResponse);
    }

    @Transactional(readOnly = true)
    public List<UserAccountResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserEntity::toResponse)
                .toList();
    }

    @Transactional
    public UserAccountResponse create(UserAccountRequest request) {
        String hash = PasswordHasher.hash(request.password());
        UserEntity entity = UserEntity.fromRequest(request, hash);
        if (entity.getStatus() == null || entity.getStatus().isBlank()) {
            entity.setStatus("ACTIVE");
        }
        return userRepository.save(entity).toResponse();
    }

    /**
     * Cập nhật tài khoản. Nếu password rỗng/null thì giữ nguyên mật khẩu cũ.
     */
    @Transactional
    public UserAccountResponse update(long id, UserAccountRequest request) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản id=" + id));

        entity.setUsername(request.username());
        entity.setRole(request.role() != null ? request.role() : Role.USER);
        if (request.status() != null && !request.status().isBlank()) {
            entity.setStatus(request.status());
        }
        if (request.password() != null && !request.password().isBlank()) {
            entity.setPasswordHash(PasswordHasher.hash(request.password()));
        }
        return userRepository.save(entity).toResponse();
    }

    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }
}
