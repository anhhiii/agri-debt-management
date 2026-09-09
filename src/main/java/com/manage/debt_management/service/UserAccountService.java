package com.manage.debt_management.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.manage.debt_management.dto.RegisterRequestDTO;
import com.manage.debt_management.dto.UpdateRequestDTO;
import com.manage.debt_management.dto.UserAccountResponseDTO;
import com.manage.debt_management.enums.ERole;
import com.manage.debt_management.model.Role;
import com.manage.debt_management.model.UserAccount;
import com.manage.debt_management.repository.RefreshTokenRepository;
import com.manage.debt_management.repository.RoleRepository;
import com.manage.debt_management.repository.UserAccountRepository;

@Service
public class UserAccountService {

    private static final int MIN_PASSWORD_LENGTH = 6;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public List<UserAccountResponseDTO> findAll() {
        return userAccountRepository.findAll().stream()
                .map(UserAccountResponseDTO::from)
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        UserAccount u = userAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        refreshTokenRepository.deleteByUser(u);
        userAccountRepository.delete(u);
    }

    public UserAccountResponseDTO register(RegisterRequestDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new IllegalArgumentException("Thiếu tên đăng nhập");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Thiếu email");
        }
        if (dto.getRole() == null) {
            throw new IllegalArgumentException("Thiếu vai trò (role)");
        }

        String username = dto.getUsername().trim();
        String email = dto.getEmail().trim().toLowerCase();

        if (userAccountRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (userAccountRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            String phone = dto.getPhone().trim();
            if (userAccountRepository.findByPhone(phone).isPresent()) {
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
            }
        }

        Role role = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò: " + dto.getRole()));

        String now = Instant.now().toString();
        UserAccount u = new UserAccount();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setEmail(email);
        u.setPhone(dto.getPhone() != null && !dto.getPhone().isBlank() ? dto.getPhone().trim() : null);
        u.setRole(role);
        u.setStatus("active");
        u.setCreatedAt(now);
        u.setUpdatedAt(now);

        UserAccount saved = userAccountRepository.save(u);
        return UserAccountResponseDTO.from(saved);
    }

    public UserAccountResponseDTO update(String id, UpdateRequestDTO dto) {
        UserAccount u = userAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String email = dto.getEmail().trim().toLowerCase();
            Optional<UserAccount> other = userAccountRepository.findByEmail(email);
            if (other.isPresent() && !other.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác");
            }
            u.setEmail(email);
        }

        if (dto.getPhone() != null) {
            if (dto.getPhone().isBlank()) {
                u.setPhone(null);
            } else {
                String phone = dto.getPhone().trim();
                Optional<UserAccount> other = userAccountRepository.findByPhone(phone);
                if (other.isPresent() && !other.get().getId().equals(id)) {
                    throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi tài khoản khác");
                }
                u.setPhone(phone);
            }
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (dto.getPassword().length() < MIN_PASSWORD_LENGTH) {
                throw new IllegalArgumentException("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
            }
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRole() != null) {
            Role role = roleRepository.findByName(dto.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò: " + dto.getRole()));
            u.setRole(role);
        }

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            u.setStatus(dto.getStatus().trim());
        }

        u.setUpdatedAt(Instant.now().toString());
        UserAccount saved = userAccountRepository.save(u);
        return UserAccountResponseDTO.from(saved);
    }
}
