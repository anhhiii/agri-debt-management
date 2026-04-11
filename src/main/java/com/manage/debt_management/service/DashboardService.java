package com.manage.debt_management.service;

import com.manage.debt_management.dto.DashboardOverviewDTO;
import com.manage.debt_management.enums.ContractStatus;
import com.manage.debt_management.model.InstallmentContract;
import com.manage.debt_management.repository.InstallmentContractRepository;
import com.manage.debt_management.util.ContractLoanCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InstallmentContractRepository contractRepository;

    public DashboardOverviewDTO getOverviewStats() {
        List<InstallmentContract> allContracts = contractRepository.findAll();

        long activeCount = 0;
        long overdueCount = 0;
        long completedCount = 0;
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalRemaining = BigDecimal.ZERO;

        for (InstallmentContract c : allContracts) {
            if (c.getStatus() == ContractStatus.ACTIVE)
                activeCount++;
            else if (c.getStatus() == ContractStatus.OVERDUE)
                overdueCount++;
            else if (c.getStatus() == ContractStatus.COMPLETED)
                completedCount++;

            ContractLoanCalculator.LoanDetailsResult details = ContractLoanCalculator.calculate(c);

            BigDecimal down = c.getDownPayment() == null ? BigDecimal.ZERO : c.getDownPayment();
            totalCollected = totalCollected.add(details.getTotalPaid()).add(down);

            BigDecimal rem = details.getRemainingAmount();
            if (rem.compareTo(BigDecimal.ZERO) > 0) {
                totalRemaining = totalRemaining.add(rem);
            }
        }

        return DashboardOverviewDTO.builder()
                .totalContracts(allContracts.size())
                .activeCount(activeCount)
                .overdueCount(overdueCount)
                .completedCount(completedCount)
                .totalCollected(totalCollected)
                .totalRemaining(totalRemaining)
                .build();
    }
}