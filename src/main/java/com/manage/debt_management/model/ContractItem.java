package com.manage.debt_management.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ContractItem {
    private String productName; // VD: Phân Ure Cà Mau
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal; // quantity * unitPrice
}