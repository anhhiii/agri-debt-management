package com.manage.debt_management.dto;

import java.util.List;

import com.manage.debt_management.enums.ERole;

import lombok.Data;

@Data
public class RoleCreateRequestDTO {

    private ERole name;
    private String description;
    /** Các mã đã có trong collection {@code permissions} (ví dụ {@code /api/v1/users:POST}). */
    private List<String> permissionCodes;
}
