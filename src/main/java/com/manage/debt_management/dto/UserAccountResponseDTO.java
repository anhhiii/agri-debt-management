package com.manage.debt_management.dto;

import com.manage.debt_management.enums.ERole;
import com.manage.debt_management.model.UserAccount;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAccountResponseDTO {

    private String id;
    private String username;
    private String email;
    private String phone;
    private ERole role;
    private String status;
    private String createdAt;
    private String updatedAt;

    public static UserAccountResponseDTO from(UserAccount u) {
        ERole roleName = u.getRole() != null ? u.getRole().getName() : null;
        return UserAccountResponseDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(roleName)
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
