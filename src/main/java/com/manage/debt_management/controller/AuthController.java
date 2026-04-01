package com.manage.debt_management.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.debt_management.repository.UserAccountRepository;
import com.manage.debt_management.security.JwtUtils;
import com.manage.debt_management.service.AuthService;
import com.manage.debt_management.service.RefreshTokenService;
import com.manage.debt_management.shared.UserServiceShared;
import com.manage.debt_management.constant.HttpStatusConstants;
import com.manage.debt_management.dto.LoginRequestDTO;
import com.manage.debt_management.dto.ResponseApi;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UserServiceShared userServiceShared;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        String email = body.getEmail();
        if(!userServiceShared.isEmailExists(email)) {
            return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST).body(ResponseApi.error("Email không tồn tại"));
        }

        Map<String, String> tokens = authService.login(body);
        if(tokens == null) {
            return ResponseEntity.status(HttpStatusConstants.UNAUTHORIZED).body(ResponseApi.error("Sai tên đăng nhập hoặc mật khẩu"));
        }
        return ResponseEntity.status(HttpStatusConstants.OK).body(ResponseApi.ok("Đăng nhập thành công", Map.of(
            "accessToken", tokens.get("accessToken"),
            "refreshToken", tokens.get("refreshToken")
        )));
        }

        @PostMapping("/refreshtoken")
        public ResponseEntity<?> refreshtoken(@RequestBody Map<String, String> request) {
            String requestRefreshToken = request.get("refreshToken");
            Map<String, String> tokens = refreshTokenService.refreshToken(requestRefreshToken);
            return ResponseEntity.status(HttpStatusConstants.OK).body(ResponseApi.ok("Refresh token thành công", Map.of(
                "accessToken", tokens.get("accessToken"),
                "refreshToken", tokens.get("refreshToken")
            )));
        }
    

         
}