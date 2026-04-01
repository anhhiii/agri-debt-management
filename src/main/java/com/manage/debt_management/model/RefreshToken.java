package com.manage.debt_management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import java.time.Instant;

@Data
@Document(collection = "refreshtokens")
public class RefreshToken {
    @Id
    private String id;

    @DBRef
    private UserAccount user; // Liên kết với tài khoản nào

    @Indexed(unique = true)
    private String token; // Chuỗi token ngẫu nhiên

    private Instant expiryDate; // Thời điểm hết hạn
}