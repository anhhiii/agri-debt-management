package com.manage.debt_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.manage.debt_management.dto.ContractRequestDTO;
import com.manage.debt_management.dto.ContractResponseDTO;
import com.manage.debt_management.dto.PaymentRecordResponseDTO;
import com.manage.debt_management.dto.ResponseApi;
import com.manage.debt_management.enums.InterestType;
import com.manage.debt_management.model.InstallmentContract;
import com.manage.debt_management.model.PaymentRecord;
import com.manage.debt_management.service.InstallmentContractService;
import com.manage.debt_management.util.ContractLoanCalculator;
import com.manage.debt_management.util.ContractLoanCalculator.LoanDetailsResult;
import com.manage.debt_management.util.ContractLoanCalculator.PaymentAllocation;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {

    @Autowired
    private InstallmentContractService contractService;
    

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseApi<ContractResponseDTO>> getContractDetail(@PathVariable String id) {
        try {
            InstallmentContract contract = contractService.findById(id);
            return ResponseEntity.ok(
                    ResponseApi.ok("Lấy thông tin hợp đồng thành công", toContractResponseDto(contract)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error("Hợp đồng không tồn tại"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseApi<?>> delete(@PathVariable String id) {
        try {
            contractService.deleteById(id);
            return ResponseEntity.ok(ResponseApi.ok("Xoá thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseApi.error("Lỗi khi xoá" + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseApi<?>> create(@RequestBody ContractRequestDTO req) {
        try {
            InterestType interestType = req.getInterestType() != null
                    ? req.getInterestType()
                    : InterestType.SIMPLE;

            InstallmentContract contract = InstallmentContract.builder()
                    .customerId(req.getCustomerId())
                    .customerName(req.getCustomerName())
                    .customerPhone(req.getCustomerPhone())
                    .items(req.getItems())
                    .downPayment(req.getDownPayment())
                    .interestRate(req.getInterestRate())
                    .interestType(interestType)
                    .startDate(req.getStartDate())
                    .endDate(req.getEndDate())
                    .note(req.getNote())
                    .createdBy(req.getCreatedBy())
                    .build();

            InstallmentContract created = contractService.create(contract);

            ContractResponseDTO res = toContractResponseDto(created);

            return ResponseEntity.ok(
                    ResponseApi.ok("Tạo hợp đồng thành công", res));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseApi.error("Tạo hợp đồng thất bại: " + e.getMessage()));
        }
    }

    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PreAuthorize("hasAuthority('/api/v1/contracts:GET')")
    public ResponseEntity<ResponseApi<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String customerId) {
        try {
            Page<InstallmentContract> result = contractService.getAll(page, size, customerId);

            Page<ContractResponseDTO> dtoPage = result.map(this::toContractResponseDto);

            return ResponseEntity.ok(
                    ResponseApi.ok("Lấy danh sách thành công", dtoPage));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseApi.error("Lỗi lấy danh sách: " + e.getMessage()));
        }
    }

    private ContractResponseDTO toContractResponseDto(InstallmentContract c) {
        String productName = "";
        if (c.getItems() != null && !c.getItems().isEmpty()) {
            productName = c.getItems().get(0).getProductName();
        }
        ContractLoanCalculator.LoanDetailsResult details = ContractLoanCalculator.calculate(c);
        java.math.BigDecimal interestPerDay = java.math.BigDecimal.ZERO;
        if (details.getTotalDays() > 0) {
            interestPerDay = details.getTotalInterest()
                    .divide(java.math.BigDecimal.valueOf(details.getTotalDays()), 2, java.math.RoundingMode.HALF_UP);
        }
        return ContractResponseDTO.builder()
                .id(c.getId())
                .customerId(c.getCustomerId())
                .customerName(c.getCustomerName())
                .customerPhone(c.getCustomerPhone())
                .productName(productName)
                .totalValue(c.getTotalValue())
                .downPayment(c.getDownPayment())
                .principal(details.getInitialPrincipal())
                .remainingPrincipal(details.getRemainingPrincipal())
                .remainingInterest(details.getRemainingInterest())
                .interestRate(c.getInterestRate())
                .interestType(c.getInterestType() != null ? c.getInterestType().name() : null)
                .status(c.getStatus().name())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .createdBy(c.getCreatedBy())
                .note(c.getNote())
                .items(c.getItems())
                .paymentRecords(toPaymentRecordDtos(c, details))
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .totalDays(details.getTotalDays())
                .interestPerDay(interestPerDay)
                .totalInterest(details.getTotalInterest())
                .totalPaid(details.getTotalPaid())
                .totalExpected(details.getTotalExpected())
                .remainingAmount(details.getRemainingAmount())
                .loanStatus(details.getLoanStatus())
                .build();
    }

    private List<PaymentRecordResponseDTO> toPaymentRecordDtos(InstallmentContract c, LoanDetailsResult details) {
        List<PaymentRecord> raw = c.getPaymentRecords();
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        List<PaymentRecord> sorted = new ArrayList<>(raw);
        sorted.sort(Comparator
                .comparing(PaymentRecord::getPaidAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(pr -> pr.getId() != null ? pr.getId() : ""));
        BigDecimal initial = details.getInitialPrincipal();
        BigDecimal totalInterest = details.getTotalInterest();
        BigDecimal cumulative = BigDecimal.ZERO;
        List<PaymentRecordResponseDTO> out = new ArrayList<>();
        for (PaymentRecord pr : sorted) {
            BigDecimal amt = pr.getAmount() != null ? pr.getAmount() : BigDecimal.ZERO;
            PaymentAllocation alloc = ContractLoanCalculator.marginalAllocation(initial, totalInterest, cumulative,
                    amt);
            cumulative = cumulative.add(amt);
            out.add(PaymentRecordResponseDTO.builder()
                    .id(pr.getId())
                    .amount(pr.getAmount())
                    .paidAt(pr.getPaidAt())
                    .note(pr.getNote())
                    .receivedBy(pr.getReceivedBy())
                    .appliedToInterest(alloc.getAppliedToInterest())
                    .appliedToPrincipal(alloc.getAppliedToPrincipal())
                    .build());
        }
        return out;
    }
}