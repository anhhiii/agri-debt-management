package com.manage.debt_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.manage.debt_management.enums.InterestType;
import com.manage.debt_management.model.ContractItem;

import lombok.Data;

@Data
public class ContractRequestDTO {

	 private String customerId;
    private String customerName;
    private String customerPhone;
    private List<ContractItem> items;
    private BigDecimal downPayment;
    private BigDecimal interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String createdBy;
    /** Lãi đơn / lãi kép — mặc định SIMPLE nếu không gửi */
    private InterestType interestType;
    private String note;
}