package com.manage.debt_management.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.manage.debt_management.model.UserAccount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private String id;
    private String username;
    
    @JsonIgnore
    private String password;
    
    // Nơi chứa danh sách quyền (Permissions) của user
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    // Hàm chuyển đổi từ User (MongoDB) sang UserDetailsImpl
    public static UserDetailsImpl build(UserAccount user) {
        // Mỗi phần tử là mã permission (code), ví dụ /api/v1/users:POST
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole().getPermissions() != null) {
            authorities = user.getRole().getPermissions().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name())); // Thêm quyền ROLE_ vào danh sách
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return username; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; } // Thực tế có thể lấy từ trường isActive của User
}