package com.manage.debt_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.manage.debt_management.model.RefreshToken;
import com.manage.debt_management.repository.RefreshTokenRepository;
import com.manage.debt_management.repository.UserAccountRepository;
import com.manage.debt_management.security.JwtUtils;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${spring.jwt.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String email) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userAccountRepository.findByEmail(email).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString()); // Tạo chuỗi ngẫu nhiên

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn. Vui lòng đăng nhập lại!");
        }
        return token;
    }

    public Map<String, String> refreshToken(String requestRefreshToken) {
        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateJwtToken(user.getEmail(), user.getRole().getName());
                    return Map.of(
                            "accessToken", token,
                            "refreshToken", requestRefreshToken
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại!"));
    }
}