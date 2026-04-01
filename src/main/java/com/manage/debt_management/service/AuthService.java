package com.manage.debt_management.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.manage.debt_management.dto.LoginRequestDTO;
import com.manage.debt_management.model.RefreshToken;
import com.manage.debt_management.model.UserAccount;
import com.manage.debt_management.repository.UserAccountRepository;
import com.manage.debt_management.security.JwtUtils;


@Service
public class AuthService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public Map<String, String> login(LoginRequestDTO body) {
        String email = body.getEmail();
        String rawPassword = body.getPassword();

        Optional<UserAccount> userOpt = userAccountRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword())) {
            UserAccount user = userOpt.get();
            String jwt = jwtUtils.generateJwtToken(user.getEmail(), user.getRole().getName());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
            // Nếu đúng pass, nặn ra cái Token
            return Map.of("accessToken", jwt, "refreshToken", refreshToken.getToken());
        } else {
            return null;
        }
    }
}