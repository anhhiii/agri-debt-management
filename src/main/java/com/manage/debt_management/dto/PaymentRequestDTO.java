package com.manage.debt_management.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private String contractId;    // ID của hợp đồng nợ
    private BigDecimal amount;     // Số tiền khách trả lần này
    private String note;           // Ghi chú (ví dụ: "Trả bớt tiền phân bón")
    private String receivedBy;     // Tên nhân viên thu tiền
}