package com.manage.debt_management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter đảm bảo filter này chỉ chạy đúng 1 lần cho mỗi request
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String method = request.getMethod();
            String path = request.getRequestURI();
            String query = request.getQueryString();
            String origin = request.getHeader("Origin");
            String contentType = request.getContentType();
            String remoteAddr = request.getRemoteAddr();
            String acrMethod = request.getHeader("Access-Control-Request-Method");
            String acrHeaders = request.getHeader("Access-Control-Request-Headers");

            // 1. Lấy token từ header
            String headerAuth = request.getHeader("Authorization");
            String jwt = parseJwt(request);

            System.out.println(String.format(
                    "REQ %s %s%s | remote=%s | origin=%s | contentType=%s | ACR-Method=%s | ACR-Headers=%s | hasAuthorizationHeader=%s | hasBearerToken=%s",
                    method, path,
                    (query == null ? "" : "?" + query),
                    remoteAddr,
                    origin,
                    contentType,
                    acrMethod,
                    acrHeaders,
                    StringUtils.hasText(headerAuth),
                    jwt != null));
            
            // 2. Nếu có token và token hợp lệ (chưa hết hạn, không bị sửa đổi)
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Lấy email từ token
                String email = jwtUtils.getEmailFromJwtToken(jwt);

                // Kéo thông tin user (kèm theo các quyền/permissions) từ DB lên
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                System.out.println(String.format("JWT valid for email=%s | authorities=%s", email, userDetails.getAuthorities()));
                
                // 3. Đóng dấu "Đã xác thực" và nạp vào SecurityContext
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println(String.format("JWT missing/invalid for %s %s", request.getMethod(), request.getRequestURI()));
            }
        } catch (Exception e) {
            System.err.println(String.format("Không thể thiết lập xác thực cho request %s %s: %s",
                    request.getMethod(), request.getRequestURI(), e));
            e.printStackTrace(System.err);
        }

        // 4. Cho phép request đi tiếp vào Controller (nếu token sai thì Context trống, sẽ bị chặn sau)
        filterChain.doFilter(request, response);

        System.out.println(String.format("RES %s %s -> status=%s", request.getMethod(), request.getRequestURI(), response.getStatus()));
    }

    // Hàm phụ: bóc tách chữ "Bearer " ra để lấy nguyên cái chuỗi Token
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}