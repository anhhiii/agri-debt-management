package com.manage.debt_management.dto;

import com.manage.debt_management.enums.ECustomerTier;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Thông tin khách + tổng hợp nợ/lãi + danh sách hợp đồng (một response cho trang chi tiết).
 */
@Data
@Builder
public class CustomerDetailResponseDTO {

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

    /** Tổng “còn lại phải thu” theo từng hợp đồng (công thức giống UI). */
    private BigDecimal totalRemainingDebt;
    /** Tổng lãi dự kiến (theo từng hợp đồng). */
    private BigDecimal totalExpectedInterest;

    private List<CustomerContractSummaryDTO> contracts;
}