package com.manage.debt_management.dto;

import com.manage.debt_management.enums.ERole;

import lombok.Data;

/** Cập nhật tài khoản: trường {@code null} = giữ nguyên; {@code password} rỗng/null = không đổi mật khẩu. */
@Data
public class UpdateRequestDTO {

    private String email;
    private String phone;
    /** Đặt mật khẩu mới; {@code null} hoặc rỗng = không đổi. */
    private String password;
    private ERole role;
    private String status;
}
