package com.manage.debt_management.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.debt_management.constant.HttpStatusConstants;
import com.manage.debt_management.dto.LoginRequestDTO;
import com.manage.debt_management.dto.RegisterRequestDTO;
import com.manage.debt_management.dto.ResponseApi;
import com.manage.debt_management.dto.UpdateRequestDTO;
import com.manage.debt_management.dto.UserAccountResponseDTO;
import com.manage.debt_management.service.AuthService;
import com.manage.debt_management.service.RefreshTokenService;
import com.manage.debt_management.service.UserAccountService;
import com.manage.debt_management.shared.UserServiceShared;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserServiceShared userServiceShared;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserAccountService userAccountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        String email = body.getEmail();
        if (!userServiceShared.isEmailExists(email)) {
            return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST).body(ResponseApi.error("Email không tồn tại"));
        }

        Map<String, String> tokens = authService.login(body);
        if (tokens == null) {
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

    @PostMapping("/register")
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).AUTH_REGISTER_POST)")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {
        try {
            UserAccountResponseDTO data = userAccountService.register(body);
            return ResponseEntity.status(HttpStatusConstants.OK).body(ResponseApi.ok("Đăng ký thành công", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST).body(ResponseApi.error("Đăng ký thất bại: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST).body(ResponseApi.error("Đăng ký thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).AUTH_ID_PUT)")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody UpdateRequestDTO body) {
        try {
            UserAccountResponseDTO data = userAccountService.update(id, body);
            return ResponseEntity.status(HttpStatusConstants.OK).body(ResponseApi.ok("Cập nhật thành công", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST).body(ResponseApi.error("Cập nhật thất bại: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusConstants.BAD_REQUEST).body(ResponseApi.error("Cập nhật thất bại: " + e.getMessage()));
        }
    }
}