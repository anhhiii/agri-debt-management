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
}