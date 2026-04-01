package com.manage.debt_management.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentRecord {
    // Tự sinh 1 ID nhỏ để sau này lỡ có nhập sai tiền, có ID để tìm mà sửa/xóa
    private String id = UUID.randomUUID().toString(); 
    
    private BigDecimal amount; // Số tiền khách đem trả
    private LocalDateTime paidAt;
    private String note;
    private String receivedBy; // Lưu lại tên nhân viên nào đã thu cục tiền này
}