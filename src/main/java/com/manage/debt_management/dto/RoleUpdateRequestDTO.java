package com.manage.debt_management.dto;

import java.util.List;

import lombok.Data;

@Data
public class RoleUpdateRequestDTO {

    private String description;
    /**
     * Thay thế toàn bộ danh sách mã quyền. {@code null} = không đổi; {@code []} = xóa hết.
     */
    private List<String> permissionCodes;
}
