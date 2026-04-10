package com.manage.debt_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

/** Một dòng hợp đồng trong trang chi tiết khách (đủ để hiển thị + điều hướng). */
@Data
@Builder
public class CustomerContractSummaryDTO {

    private String id;
    /** Tên mặt hàng (có thể ghép nhiều dòng bằng " · "). */
    private String productName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal principal;
    /** Lãi dự kiến toàn kỳ (theo công thức đơn giản/kép). */
    private BigDecimal totalInterest;
    /** Còn phải thu (gốc + lãi − đã trả). */
    private BigDecimal remainingAmount;
    /** active | overdue | completed — khớp UI. */
    private String loanStatus;
}