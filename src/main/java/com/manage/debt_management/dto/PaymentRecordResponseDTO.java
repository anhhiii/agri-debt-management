package com.manage.debt_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/** Một lần thu kèm phân bổ lãi/gốc (lãi trước, gốc sau). */
@Data
@Builder
public class PaymentRecordResponseDTO {

    private String id;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String note;
    private String receivedBy;

    /** Phần khoản thu này quy vào lãi (theo tổng lãi dự kiến còn lại trước lần thu). */
    private BigDecimal appliedToInterest;
    /** Phần khoản thu này quy vào gốc (sau khi trừ lãi). */
    private BigDecimal appliedToPrincipal;
}