package com.manage.debt_management.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.manage.debt_management.enums.ERole;
import com.manage.debt_management.model.Role;

import lombok.Builder;
import lombok.Data;

/**
 * Vai trò: {@code permissions} chỉ là các <strong>mã code</strong> đã đăng ký trong catalog
 * (ví dụ {@code /api/v1/users:POST}), không embed document Permission.
 */
@Data
@Builder
public class RoleResponseDTO {

    private String id;
    private ERole name;
    private String description;
    /** Danh sách mã quyền (chuỗi), trùng với {@link com.manage.debt_management.model.Permission#getCode()}. */
    private List<String> permissionCodes;

    public static RoleResponseDTO from(Role r) {
        List<String> codes = Collections.emptyList();
        if (r.getPermissions() != null && !r.getPermissions().isEmpty()) {
            codes = new ArrayList<>(r.getPermissions());
        }
        return RoleResponseDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .permissionCodes(codes)
                .build();
    }
}
