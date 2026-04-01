package com.manage.debt_management.dto;

import com.manage.debt_management.enums.ECustomerTier;

import lombok.Data;

@Data
public class CustomerRequestDTO {

    private String name;
    private String phone;
    private String address;
    private String farmingLocation;
    private ECustomerTier customerTier;
}