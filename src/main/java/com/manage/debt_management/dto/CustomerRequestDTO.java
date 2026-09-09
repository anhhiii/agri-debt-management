package com.manage.debt_management.dto;

import com.manage.debt_management.enums.ECustomerTier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerRequestDTO {

    @NotBlank(message = "Thiếu họ tên")
    private String name;

    @NotBlank(message = "Thiếu số điện thoại")
    @Pattern(
        regexp = "^(0[35789]\\d{8})$",
        message = "Số điện thoại phải có 10 số và bắt đầu bằng 03/05/07/08/09"
    )
    private String phone;

    @NotBlank(message = "Thiếu địa chỉ")
    private String address;

    @NotBlank(message = "Thiếu vùng canh tác")
    private String farmingLocation;

    @NotNull(message = "Cần chọn hạng khách")
    private ECustomerTier customerTier;
}