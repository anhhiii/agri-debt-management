package com.manage.debt_management.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manage.debt_management.enums.ContractStatus;
import com.manage.debt_management.dto.PaymentRequestDTO;
import com.manage.debt_management.model.Customer;
import com.manage.debt_management.model.InstallmentContract;
import com.manage.debt_management.model.PaymentRecord;
import com.manage.debt_management.repository.CustomerRepository;
import com.manage.debt_management.repository.InstallmentContractRepository;
import com.manage.debt_management.util.ContractLoanCalculator;

/**
 * Service xử lý nghiệp vụ liên quan đến hợp đồng trả góp (Installment Contract).
 */
@Service
public class InstallmentContractService {

    @Autowired
    private InstallmentContractRepository iCRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Tìm kiếm hợp đồng theo ID.
     */
    public InstallmentContract findById(String id) {
        if (id != null) {
            return iCRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + id));
        }
        return null;
    }

    /**
     * Xóa hợp đồng theo ID.
     */
    public void deleteById(String id) {
        if (!iCRepository.existsById(id)) {
            throw new RuntimeException("Không thể xóa: Hợp đồng không tồn tại");
        }
        iCRepository.deleteById(id);
    }

    /**
     * Lấy danh sách hợp đồng phân trang, có thể lọc theo khách hàng.
     */
    public Page<InstallmentContract> getAll(int page, int size, String customerId) {
        Pageable pageable = PageRequest.of(page, size);
        if (customerId != null && !customerId.isBlank()) {
            return iCRepository.findByCustomerId(customerId.trim(), pageable);
        }
        return iCRepository.findAll(pageable);
    }

    /**
     * Tạo mới một hợp đồng trả góp.
     * Thực hiện tính toán tổng giá trị và nợ gốc ban đầu dựa trên danh sách sản phẩm.
     */
    public InstallmentContract create(InstallmentContract contract) {

        contract.setId(null); // Đảm bảo tạo mới thay vì cập nhật

        // Tính toán thành tiền (SubTotal) cho từng hạng mục trong hợp đồng
        contract.getItems().forEach(item -> {
            BigDecimal sub = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setSubTotal(sub);
        });

        // Tổng giá trị hợp đồng = tổng các SubTotal
        BigDecimal total = contract.getItems().stream()
                .map(item -> item.getSubTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        contract.setTotalValue(total);

        // Xác định số tiền trả trước (mặc định là 0 nếu để trống)
        BigDecimal down = contract.getDownPayment() == null
                ? BigDecimal.ZERO
                : contract.getDownPayment();

        // Nợ gốc (Principal) = Tổng giá trị - Số tiền trả trước
        BigDecimal originalPrincipal = total.subtract(down);
        if (originalPrincipal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Nợ gốc ban đầu không hợp lệ (trả trước vượt tổng giá trị)");
        }
        contract.setPrincipal(originalPrincipal);

        // Khởi tạo thời gian tạo và cập nhật
        contract.setCreatedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());

        return iCRepository.save(contract);
    }
    
    /**
     * Ghi nhận một lần thanh toán của khách hàng cho hợp đồng.
     * Sử dụng @Transactional để đảm bảo tính toàn vẹn dữ liệu khi cập nhật nhiều bảng.
     */
    @Transactional
    public InstallmentContract recordPayment(PaymentRequestDTO dto) {
        // 1. Kiểm tra sự tồn tại của hợp đồng
        InstallmentContract contract = iCRepository.findById(dto.getContractId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        // Kiểm tra số tiền thanh toán hợp lệ
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0");
        }
        
        BigDecimal amountPaid = dto.getAmount();
        
        // Sử dụng Helper để tính toán dư nợ hiện tại (bao gồm cả lãi phát sinh)
        ContractLoanCalculator.LoanDetailsResult details = ContractLoanCalculator.calculate(contract);
        BigDecimal remainingBeforePayment = details.getRemainingAmount();
        
        if (remainingBeforePayment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hợp đồng đã tất toán, không thể ghi nhận thêm thanh toán");
        }
        
        // Không cho phép trả quá số tiền còn nợ
        if (amountPaid.compareTo(remainingBeforePayment) > 0) {
            throw new IllegalArgumentException("Số tiền thanh toán vượt số còn phải thu: " + remainingBeforePayment);
        }

        // 2. Lưu lịch sử thanh toán vào danh sách của hợp đồng
        PaymentRecord record = new PaymentRecord();
        record.setAmount(amountPaid);
        record.setPaidAt(LocalDateTime.now());
        record.setNote(dto.getNote());
        record.setReceivedBy(dto.getReceivedBy());

        if (contract.getPaymentRecords() == null) {
            contract.setPaymentRecords(new ArrayList<>());
        }
        contract.getPaymentRecords().add(record);

        // 3. Cập nhật trạng thái hợp đồng dựa trên kết quả tính toán sau khi trả tiền
        // Việc gọi lại calculate giúp xác định trạng thái mới (Hoàn thành, Quá hạn, hay Đang hoạt động)
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

        // 4. Đồng bộ nợ tổng của khách hàng (Bảng Customer)
        // Phần này được đặt trong try-catch để tránh việc lỗi đồng bộ nhỏ làm hỏng giao dịch chính
        try {
            Customer customer = customerRepository.findById(contract.getCustomerId()).orElse(null);
            if (customer != null) {
                BigDecimal currentDebt = customer.getTotalCurrentDebt() != null
                        ? customer.getTotalCurrentDebt()
                        : BigDecimal.ZERO;
                
                // Trừ số nợ tương ứng
                BigDecimal nextDebt = currentDebt.subtract(amountPaid);
                if (nextDebt.compareTo(BigDecimal.ZERO) < 0) {
                    nextDebt = BigDecimal.ZERO;
                }
                customer.setTotalCurrentDebt(nextDebt);
                customerRepository.save(customer);
            }
        } catch (Exception e) {
            // Log lỗi hệ thống nhưng vẫn cho phép hoàn tất việc lưu record payment
            System.err.println("CẢNH BÁO: Lỗi cập nhật nợ tổng khách hàng: " + e.getMessage());
        }

        return iCRepository.save(contract);
    }
}