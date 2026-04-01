package com.manage.debt_management.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manage.debt_management.enums.ECustomerTier;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "customers")
public class Customer {

    @Id
    private String id; // UUID định danh khách hàng do MongoDB tự cấp

    private String name;

    @Indexed(unique = true) // Đảm bảo số điện thoại không trùng và tìm kiếm siêu tốc
    private String phone;

    private String address;
    private String farmingLocation; // Vị trí canh tác/Vùng trồng trọt

    // Enum định nghĩa hạng khách hàng (STANDARD, REGULAR, VIP)
    private ECustomerTier customerTier = ECustomerTier.STANDARD;

    // --- Các trường thống kê (Aggregated Fields) để truy vấn nhanh ---

    // Ngày giao dịch gần nhất
    private LocalDateTime lastPurchaseDate;

    // Tổng số tiền đang nợ hiện tại (Cần update mỗi khi tạo hợp đồng hoặc khách trả
    // tiền)
    private BigDecimal totalCurrentDebt = BigDecimal.ZERO;

    // Tổng giá trị hàng hóa đã từng mua (Cần update mỗi khi tạo hợp đồng mới)
    private BigDecimal lifetimePurchaseValue = BigDecimal.ZERO;

    // -----------------------------------------------------------------

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Nên có field này để biết lần cuối sửa thông tin là khi nào
}
