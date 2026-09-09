package com.manage.debt_management.dto;

/**
 * Kết quả đồng bộ catalog permission từ các mapping Spring MVC vào collection {@code permissions}.
 */
public record PermissionSyncResultDTO(int inserted, int skipped) {}
