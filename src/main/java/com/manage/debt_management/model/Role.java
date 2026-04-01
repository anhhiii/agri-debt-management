package com.manage.debt_management.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vai trò: {@code permissions} chỉ lưu các <strong>mã code</strong> (chuỗi) trùng với
 * {@link Permission#getCode()}, không tham chiếu document.
 */
@Data
@NoArgsConstructor
@Document(collection = "roles")
public class Role {

    @Id
    private String id;

    @Indexed(unique = true)
    private com.manage.debt_management.enums.ERole name;

    private String description;

    /** Các mã quyền, ví dụ {@code /api/v1/users:POST}. */
    private List<String> permissions = new ArrayList<>();
}