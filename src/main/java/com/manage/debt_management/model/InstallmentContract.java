package com.manage.debt_management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manage.debt_management.enums.ContractStatus;
import com.manage.debt_management.enums.InterestType;
import org.bson.types.ObjectId;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Document(collection = "installment_contracts")
public class InstallmentContract {

    @Id
    private String id;

    @Indexed // Đánh index ID khách hàng để tìm toàn bộ nợ của 1 người cho nhanh
    private String customerId; 
    private String customerName;  // Lưu kèm tên để hiển thị ngay không cần query DB
    private String customerPhone; // Lưu kèm SĐT
    // -------------------------------------------

    // Nhúng danh sách vật tư đã mua
    private List<ContractItem> items = new ArrayList<>();

    // Các thông số tài chính của hợp đồng
    private BigDecimal totalValue;      // Tổng giá trị đơn hàng
    private BigDecimal downPayment;     // Tiền trả trước (Trả ngay lúc mua)
    /** Nợ gốc ban đầu (totalValue − downPayment). Không cập nhật khi thu tiền — dùng làm snapshot + fallback khi query. */
    private BigDecimal principal;
    private BigDecimal interestRate;    // Lãi suất (%/tháng)

    private InterestType interestType = InterestType.SIMPLE;
    @Builder.Default
    private ContractStatus status = ContractStatus.ACTIVE;

    private LocalDate startDate;        // Ngày bắt đầu mượn/Ngày xuất kho
    private LocalDate endDate;          // Ngày hẹn trả (Thường là cuối vụ mùa)

    // Nhúng lịch sử những lần khách đem tiền đến trả lắt nhắt
    private List<PaymentRecord> paymentRecords = new ArrayList<>();

    private String note;                // Ghi chú hợp đồng
    private String createdBy;           // Người tạo phiếu nợ (Nhân viên/Chủ)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}