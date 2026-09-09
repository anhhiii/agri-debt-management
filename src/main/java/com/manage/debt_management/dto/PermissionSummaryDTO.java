package com.manage.debt_management.dto;

import com.manage.debt_management.model.Permission;

import lombok.Builder;
import lombok.Data;

/** Bản ghi permission trong DB (catalog endpoint) — dùng cho GET /api/v1/permissions. */
@Data
@Builder
public class PermissionSummaryDTO {

    private String id;
    private String code;
    private String description;
    private String module;

    public static PermissionSummaryDTO from(Permission p) {
        if (p == null) {
            return null;
        }
        return PermissionSummaryDTO.builder()
                .id(p.getId())
                .code(p.getCode())
                .description(p.getDescription())
                .module(p.getModule())
                .build();
    }
}
