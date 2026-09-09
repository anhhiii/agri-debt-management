package com.manage.debt_management.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.manage.debt_management.enums.ContractStatus;
import com.manage.debt_management.enums.InterestType;
import com.manage.debt_management.dto.PaymentRequestDTO;
import com.manage.debt_management.model.Customer;
import com.manage.debt_management.model.InstallmentContract;
import com.manage.debt_management.model.PaymentRecord;
import com.manage.debt_management.repository.CustomerRepository;
import com.manage.debt_management.repository.InstallmentContractRepository;
import com.manage.debt_management.util.ContractLoanCalculator;

@Service
public class InstallmentContractService {

    @Autowired
    private InstallmentContractRepository iCRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    public InstallmentContract findById(String id) {
        if (id != null) {
            return iCRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        }
        return null;
    }

    /**
     * Xóa hợp đồng chỉ khi đã kết thúc nợ: trạng thái {@link ContractStatus#COMPLETED} / {@link ContractStatus#CANCELLED},
     * hoặc đã thu đủ (gốc + lãi đơn/kép khớp cách tính trên UI). Hợp đồng đang còn dư nợ thì không xóa.
     */
    public void deleteById(String id) {
        InstallmentContract existing = iCRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng"));
        if (!mayDeleteContract(existing)) {
            throw new IllegalStateException(
                    "Không thể xóa hợp đồng đang còn nợ. Chỉ xóa khi đã tất toán, đã hủy, hoặc đã thu đủ tiền.");
        }
        iCRepository.delete(existing);
    }

    private static boolean mayDeleteContract(InstallmentContract c) {
        ContractStatus st = c.getStatus() != null ? c.getStatus() : ContractStatus.ACTIVE;
        if (st == ContractStatus.COMPLETED || st == ContractStatus.CANCELLED) {
            return true;
        }
        return isFinanciallySettled(c);
    }

    /**
     * Khớp {@code calculateLoanDetailsFromContract} (lãi %/tháng, lãi đơn / lãi kép) — dùng để biết đã thu đủ chưa.
     */
    private static boolean isFinanciallySettled(InstallmentContract c) {
        BigDecimal principal = nz(c.getPrincipal());
        LocalDate start = c.getStartDate();
        LocalDate end = c.getEndDate();
        if (principal.signum() <= 0 || start == null || end == null) {
            return false;
        }
        long totalDays = ChronoUnit.DAYS.between(start, end);
        if (totalDays < 0) {
            totalDays = 0;
        }
        BigDecimal ratePct = nz(c.getInterestRate());
        double r = ratePct.doubleValue() / 100.0;
        BigDecimal totalInterest;
        InterestType it = c.getInterestType() != null ? c.getInterestType() : InterestType.SIMPLE;
        if (it == InterestType.SIMPLE) {
            BigDecimal interestPerDay = principal.multiply(BigDecimal.valueOf(r / 30.0));
            totalInterest = interestPerDay.multiply(BigDecimal.valueOf(totalDays));
        } else {
            double dailyRate = r / 30.0;
            double ti = principal.doubleValue() * (Math.pow(1 + dailyRate, totalDays) - 1.0);
            totalInterest = BigDecimal.valueOf(ti);
        }
        BigDecimal totalExpected = principal.add(totalInterest).setScale(0, RoundingMode.DOWN);
        BigDecimal totalPaid = sumPaymentAmounts(c);
        return totalPaid.compareTo(totalExpected) >= 0;
    }

    private static BigDecimal sumPaymentAmounts(InstallmentContract c) {
        if (c.getPaymentRecords() == null || c.getPaymentRecords().isEmpty()) {
            return BigDecimal.ZERO.setScale(0, RoundingMode.DOWN);
        }
        return c.getPaymentRecords().stream()
                .map(PaymentRecord::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.DOWN);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    public Page<InstallmentContract> getAll(int page, int size, String customerId) {
        Pageable pageable = PageRequest.of(page, size);
        if (customerId != null && !customerId.isBlank()) {
            return iCRepository.findByCustomerId(customerId.trim(), pageable);
        }
        return iCRepository.findAll(pageable);
    }

    public InstallmentContract create(InstallmentContract contract) {

        contract.setId(null);

        contract.getItems().forEach(item -> {
            BigDecimal sub = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setSubTotal(sub);
        });

        BigDecimal total = contract.getItems().stream()
                .map(item -> item.getSubTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        contract.setTotalValue(total);

        BigDecimal down = contract.getDownPayment() == null
                ? BigDecimal.ZERO
                : contract.getDownPayment();

        BigDecimal originalPrincipal = total.subtract(down);
        if (originalPrincipal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Nợ gốc ban đầu không hợp lệ (trả trước vượt tổng giá trị)");
        }
        contract.setPrincipal(originalPrincipal);

        contract.setCreatedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());

        return iCRepository.save(contract);
    }
    
    @Transactional
    public InstallmentContract recordPayment(PaymentRequestDTO dto) {
        // 1. Tìm hợp đồng
        InstallmentContract contract = iCRepository.findById(dto.getContractId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0");
        }
        BigDecimal amountPaid = dto.getAmount();
        ContractLoanCalculator.LoanDetailsResult details = ContractLoanCalculator.calculate(contract);
        BigDecimal remainingBeforePayment = details.getRemainingAmount();
        if (remainingBeforePayment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hợp đồng đã tất toán, không thể ghi nhận thêm thanh toán");
        }
        if (amountPaid.compareTo(remainingBeforePayment) > 0) {
            throw new IllegalArgumentException("Số tiền thanh toán vượt số còn phải thu");
        }

        // 2. GHI NHẬN LỊCH SỬ TRẢ TIỀN (phân bổ lãi/gốc do ContractLoanCalculator suy ra)
        PaymentRecord record = new PaymentRecord();
        record.setAmount(amountPaid);
        record.setPaidAt(LocalDateTime.now());
        record.setNote(dto.getNote());
        record.setReceivedBy(dto.getReceivedBy());

        if (contract.getPaymentRecords() == null) {
            contract.setPaymentRecords(new ArrayList<>());
        }
        contract.getPaymentRecords().add(record);

        // 3. TRẠNG THÁI THEO remainingAmount (gốc + lãi)
        ContractLoanCalculator.LoanDetailsResult afterPayment = ContractLoanCalculator.calculate(contract);
        String loanStatus = afterPayment.getLoanStatus();
        if ("completed".equalsIgnoreCase(loanStatus)) {
            contract.setStatus(ContractStatus.COMPLETED);
        } else if ("overdue".equalsIgnoreCase(loanStatus)) {
            contract.setStatus(ContractStatus.OVERDUE);
        } else {
            contract.setStatus(ContractStatus.ACTIVE);
        }

        contract.setUpdatedAt(LocalDateTime.now());

        // 4. CẬP NHẬT NỢ TỔNG CỦA KHÁCH HÀNG (Ở bảng Customer)
        try {
            Customer customer = customerRepository.findById(contract.getCustomerId()).orElse(null);
            if (customer != null) {
                // Trừ số tiền tương ứng vào tổng nợ hiện tại của khách
                BigDecimal currentDebt = customer.getTotalCurrentDebt() != null
                        ? customer.getTotalCurrentDebt()
                        : BigDecimal.ZERO;
                BigDecimal nextDebt = currentDebt.subtract(amountPaid);
                if (nextDebt.compareTo(BigDecimal.ZERO) < 0) {
                    nextDebt = BigDecimal.ZERO;
                }
                customer.setTotalCurrentDebt(nextDebt);
                customerRepository.save(customer);
            }
        } catch (Exception e) {
            // Log lỗi nhưng không làm fail transaction trả tiền của hợp đồng
            System.out.println("Lỗi cập nhật nợ tổng khách hàng: " + e.getMessage());
        }

        return iCRepository.save(contract);
    }
}