package com.manage.debt_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.manage.debt_management.model.ContractItem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractResponseDTO {
    private String id;
    /** Mã khách (để lọc / trang chi tiết khách) */
    private String customerId;
    private String customerName;
    private String customerPhone;
    /** Tên mặt hàng dòng đầu (hiển thị danh sách) */
    private String productName;
    private BigDecimal totalValue;
    private BigDecimal downPayment;
    /** Gốc ban đầu (totalValue − trả trước). */
    private BigDecimal principal;
    /** Gốc còn lại (phân bổ thu tiền: lãi trước, gốc sau). */
    private BigDecimal remainingPrincipal;
    /** Lãi còn lại (cùng quy tắc phân bổ với remainingPrincipal). */
    private BigDecimal remainingInterest;
    private BigDecimal interestRate;
    private String interestType;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String createdBy;
    private String note;
    private List<ContractItem> items;
    private List<PaymentRecordResponseDTO> paymentRecords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Số ngày giữa ngày bắt đầu và ngày kết thúc. */
    private long totalDays;
    /** Tiền lãi/ngày theo loại lãi suất. */
    private BigDecimal interestPerDay;
    /** Tổng tiền lãi dự kiến cho toàn kỳ. */
    private BigDecimal totalInterest;
    /** Tổng khách đã thanh toán. */
    private BigDecimal totalPaid;
    /** Tổng phải trả (gốc + lãi). */
    private BigDecimal totalExpected;
    /** Còn lại phải thu. */
    private BigDecimal remainingAmount;
    /** Trạng thái tính toán: active/overdue/completed. */
    private String loanStatus;
}