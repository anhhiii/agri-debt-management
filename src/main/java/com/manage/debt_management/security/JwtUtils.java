package com.manage.debt_management.security;


import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

import com.manage.debt_management.enums.ERole;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    // Khóa bí mật (Secret Key) dùng để ký Token. Trong thực tế nên để trong file application.yml
    private final String jwtSecret = "BlinkyVacaSuperSecretKeyForDebtManagementSystem2026!";
    // Thời gian sống của Token (VD: 24 giờ)
    private final int jwtExpirationMs = 86400000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Hàm tạo Token từ username (số điện thoại)
    public String generateJwtToken(String email, ERole role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Hàm lấy email từ Token
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // đọc subject, chính là email
    }

    // Hàm kiểm tra Token có hợp lệ không (có bị sửa đổi, hết hạn không)
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token không hợp lệ: " + e.getMessage());
        }
        return false;
    }
}