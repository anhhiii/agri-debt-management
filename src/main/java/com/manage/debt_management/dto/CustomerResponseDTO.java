package com.manage.debt_management.dto;


import com.manage.debt_management.enums.ECustomerTier;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponseDTO {

    private String id;
    private String name;
    private String phone;
    private String address;
    private String farmingLocation;
    private ECustomerTier customerTier;
    private LocalDateTime lastPurchaseDate;
    private BigDecimal totalCurrentDebt;
    private BigDecimal lifetimePurchaseValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}