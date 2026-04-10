package com.manage.debt_management.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.manage.debt_management.enums.InterestType;
import com.manage.debt_management.model.InstallmentContract;
import com.manage.debt_management.model.PaymentRecord;

/**
 * Khớp {@code calculateLoanDetailsFromContract} trong UI (shared/lib/contract-loan-details.ts).
 * Lãi %/tháng; đơn giản: lãi/ngày = principal * (r/30); kép: principal * ((1+r/30)^days - 1).
 */
public final class ContractLoanCalculator {

    private ContractLoanCalculator() {
    }

    public static LoanDetailsResult calculate(InstallmentContract c) {
        BigDecimal initialPrincipal = resolveInitialPrincipal(c);
        LocalDate start = c.getStartDate();
        LocalDate end = c.getEndDate();
        long totalDays = 0;
        if (start != null && end != null) {
            totalDays = ChronoUnit.DAYS.between(start, end);
        }
        if (totalDays < 0) {
            totalDays = 0;
        }

        BigDecimal ratePct = nz(c.getInterestRate());
        double r = ratePct.doubleValue() / 100.0;
        boolean simple = c.getInterestType() == null || c.getInterestType() == InterestType.SIMPLE;

        BigDecimal totalInterest;
        if (simple) {
            BigDecimal interestPerDay = initialPrincipal.multiply(BigDecimal.valueOf(r / 30.0));
            totalInterest = interestPerDay.multiply(BigDecimal.valueOf(totalDays));
        } else {
            double dailyRate = r / 30.0;
            double p = initialPrincipal.doubleValue();
            double ti = p * (Math.pow(1.0 + dailyRate, totalDays) - 1.0);
            totalInterest = BigDecimal.valueOf(ti);
        }

        BigDecimal totalPaid = BigDecimal.ZERO;
        if (c.getPaymentRecords() != null) {
            for (PaymentRecord p : c.getPaymentRecords()) {
                if (p != null && p.getAmount() != null) {
                    totalPaid = totalPaid.add(p.getAmount());
                }
            }
        }

        BigDecimal totalExpected = initialPrincipal.add(totalInterest);
        BigDecimal remainingAmount = totalExpected.subtract(totalPaid);
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }

        // Thu tiền ưu tiên lãi trước, phần còn lại mới trừ gốc
        BigDecimal paidToInterest = totalPaid.min(totalInterest);
        BigDecimal paidToPrincipal = totalPaid.subtract(paidToInterest);
        if (paidToPrincipal.compareTo(initialPrincipal) > 0) {
            paidToPrincipal = initialPrincipal;
        }
        BigDecimal remainingPrincipal = initialPrincipal.subtract(paidToPrincipal);
        if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
            remainingPrincipal = BigDecimal.ZERO;
        }

        BigDecimal remainingInterest = totalInterest.subtract(paidToInterest);
        if (remainingInterest.compareTo(BigDecimal.ZERO) < 0) {
            remainingInterest = BigDecimal.ZERO;
        }

        LocalDate today = LocalDate.now();
        boolean isOverdue = end != null && today.isAfter(end) && remainingAmount.compareTo(BigDecimal.ZERO) > 0;
        boolean isCompleted = remainingAmount.compareTo(BigDecimal.ZERO) <= 0;

        String loanStatus;
        if (isCompleted) {
            loanStatus = "completed";
        } else if (isOverdue) {
            loanStatus = "overdue";
        } else {
            loanStatus = "active";
        }

        return new LoanDetailsResult(
                totalDays,
                roundMoney(totalInterest),
                roundMoney(totalPaid),
                roundMoney(totalExpected),
                roundMoney(remainingAmount),
                loanStatus,
                roundMoney(initialPrincipal),
                roundMoney(remainingPrincipal),
                roundMoney(remainingInterest));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    /**
     * Gốc ban đầu: ưu tiên {@code totalValue − downPayment} (chuẩn khi có đủ trường). Fallback {@code principal} trên document.
     */
    private static BigDecimal resolveInitialPrincipal(InstallmentContract c) {
        if (c.getTotalValue() != null) {
            BigDecimal down = c.getDownPayment() != null ? c.getDownPayment() : BigDecimal.ZERO;
            BigDecimal fromTotal = c.getTotalValue().subtract(down);
            if (fromTotal.compareTo(BigDecimal.ZERO) >= 0) {
                return fromTotal;
            }
        }
        return nz(c.getPrincipal());
    }

    private static BigDecimal roundMoney(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Phân bổ <strong>một</strong> lần thu: lãi trước, gốc sau (khớp tổng hợp đồng).
     *
     * @param cumulativePaidBefore tổng đã thu trước lần này (không gồm {@code paymentAmount})
     */
    public static PaymentAllocation marginalAllocation(BigDecimal initialPrincipal, BigDecimal totalInterest,
            BigDecimal cumulativePaidBefore, BigDecimal paymentAmount) {
        BigDecimal p0 = nz(initialPrincipal);
        BigDecimal ti = nz(totalInterest);
        BigDecimal pb = nz(cumulativePaidBefore);
        BigDecimal amt = nz(paymentAmount);
        if (amt.compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentAllocation(roundMoney(BigDecimal.ZERO), roundMoney(BigDecimal.ZERO));
        }
        BigDecimal paidToInterestSoFar = pb.min(ti);
        BigDecimal paidToPrincipalSoFar = pb.subtract(paidToInterestSoFar);
        if (paidToPrincipalSoFar.compareTo(p0) > 0) {
            paidToPrincipalSoFar = p0;
        }
        BigDecimal remainingPrincipalBefore = p0.subtract(paidToPrincipalSoFar);
        if (remainingPrincipalBefore.compareTo(BigDecimal.ZERO) < 0) {
            remainingPrincipalBefore = BigDecimal.ZERO;
        }
        BigDecimal remainingInterestBefore = ti.subtract(paidToInterestSoFar);
        if (remainingInterestBefore.compareTo(BigDecimal.ZERO) < 0) {
            remainingInterestBefore = BigDecimal.ZERO;
        }
        BigDecimal appliedToInterest = amt.min(remainingInterestBefore);
        BigDecimal remainder = amt.subtract(appliedToInterest);
        BigDecimal appliedToPrincipal = remainder.min(remainingPrincipalBefore);
        return new PaymentAllocation(roundMoney(appliedToInterest), roundMoney(appliedToPrincipal));
    }

    /** Kết quả phân bổ một lần thu. */
    public static final class PaymentAllocation {
        private final BigDecimal appliedToInterest;
        private final BigDecimal appliedToPrincipal;

        public PaymentAllocation(BigDecimal appliedToInterest, BigDecimal appliedToPrincipal) {
            this.appliedToInterest = appliedToInterest;
            this.appliedToPrincipal = appliedToPrincipal;
        }

        public BigDecimal getAppliedToInterest() {
            return appliedToInterest;
        }

        public BigDecimal getAppliedToPrincipal() {
            return appliedToPrincipal;
        }
    }

    public static final class LoanDetailsResult {
        private final long totalDays;
        private final BigDecimal totalInterest;
        private final BigDecimal totalPaid;
        private final BigDecimal totalExpected;
        private final BigDecimal remainingAmount;
        private final String loanStatus;
        private final BigDecimal initialPrincipal;
        private final BigDecimal remainingPrincipal;
        private final BigDecimal remainingInterest;

        public LoanDetailsResult(long totalDays, BigDecimal totalInterest, BigDecimal totalPaid,
                BigDecimal totalExpected, BigDecimal remainingAmount, String loanStatus,
                BigDecimal initialPrincipal, BigDecimal remainingPrincipal, BigDecimal remainingInterest) {
            this.totalDays = totalDays;
            this.totalInterest = totalInterest;
            this.totalPaid = totalPaid;
            this.totalExpected = totalExpected;
            this.remainingAmount = remainingAmount;
            this.loanStatus = loanStatus;
            this.initialPrincipal = initialPrincipal;
            this.remainingPrincipal = remainingPrincipal;
            this.remainingInterest = remainingInterest;
        }

        public long getTotalDays() {
            return totalDays;
        }

        public BigDecimal getTotalInterest() {
            return totalInterest;
        }

        public BigDecimal getTotalPaid() {
            return totalPaid;
        }

        public BigDecimal getTotalExpected() {
            return totalExpected;
        }

        public BigDecimal getRemainingAmount() {
            return remainingAmount;
        }

        public String getLoanStatus() {
            return loanStatus;
        }

        /** Gốc ban đầu (khớp totalValue − down hoặc trường {@code principal}). */
        public BigDecimal getInitialPrincipal() {
            return initialPrincipal;
        }

        /** Gốc còn lại sau khi phân bổ thanh toán: lãi trước, gốc sau. */
        public BigDecimal getRemainingPrincipal() {
            return remainingPrincipal;
        }

        /** Lãi còn lại phải thu (tổng lãi dự kiến − phần đã quy vào lãi). */
        public BigDecimal getRemainingInterest() {
            return remainingInterest;
        }
    }
}