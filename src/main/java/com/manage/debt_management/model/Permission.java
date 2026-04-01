package com.manage.debt_management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Quyền API (permission) — ví dụ document:
 * <pre>
 * { "_id": "1", "code": "/api/v1/users:POST", "description": "Tạo nhân viên mới", "module": "Nhân sự" }
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "permissions")
public class Permission {

    @Id
    private String id;

    /**
     * Mã quyền dạng {@code path:METHOD}, ví dụ {@code /api/v1/users:POST}.
     */
    @Indexed(unique = true)
    private String code;

    private String description;

    /**
     * Nhóm / module chức năng (ví dụ "Nhân sự").
     */
    @Indexed
    private String module;
}
