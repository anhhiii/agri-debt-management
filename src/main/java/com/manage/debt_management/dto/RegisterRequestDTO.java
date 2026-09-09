package com.manage.debt_management.dto;

import com.manage.debt_management.enums.ERole;

import lombok.Data;

@Data
public class RegisterRequestDTO {

    private String username;
    private String password;
    private String email;
    /** Có thể để trống nếu không dùng. */
    private String phone;
    private ERole role;
}
