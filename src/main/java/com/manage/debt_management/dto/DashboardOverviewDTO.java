package com.manage.debt_management.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Builder
public class DashboardOverviewDTO {

    private long totalContracts;
    private long activeCount;
    private long overdueCount;
    private long completedCount;
    /** Tổng đã thu (từ lịch sử thanh toán). */
    private BigDecimal totalCollected;
    /** Tổng còn phải thu (gốc + lãi dự kiến − đã thu, theo cùng logic tính như UI). */
    private BigDecimal totalRemaining;
    /** Tổng tiền lãi dự kiến (cộng lãi từng hợp đồng, đã làm tròn theo quy tắc hệ thống). */
    private BigDecimal totalInterest;
    /** Tổng tiền thực cho vay (gốc trả góp): giá trị HĐ − trả trước, không gồm phần khách đã trả trước. */
    private BigDecimal totalPrincipalLent;
}