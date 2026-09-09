package com.manage.debt_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.debt_management.dto.PermissionSummaryDTO;
import com.manage.debt_management.dto.PermissionSyncResultDTO;
import com.manage.debt_management.dto.ResponseApi;
import com.manage.debt_management.service.PermissionCatalogService;
import com.manage.debt_management.service.PermissionCatalogSyncService;

/**
 * Danh sách permission đang có trong DB (catalog / sync endpoint) — để gán {@code code} vào role.
 */
@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    @Autowired
    private PermissionCatalogService permissionCatalogService;

    @Autowired
    private PermissionCatalogSyncService permissionCatalogSyncService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).PERMISSIONS_GET)")
    public ResponseEntity<ResponseApi<List<PermissionSummaryDTO>>> list() {
        try {
            return ResponseEntity.ok(ResponseApi.ok("Danh sách permission (catalog)", permissionCatalogService.findAllForCatalog()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error(e.getMessage()));
        }
    }

    /**
     * Quét lại toàn bộ mapping Spring MVC và chèn permission chưa có trong DB (ADMIN).
     */
    @PostMapping("/sync")
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).PERMISSIONS_SYNC_POST)")
    public ResponseEntity<ResponseApi<PermissionSyncResultDTO>> syncFromMvc() {
        try {
            PermissionSyncResultDTO r = permissionCatalogSyncService.syncMissingPermissionsFromMvc();
            return ResponseEntity.ok(ResponseApi.ok("Đồng bộ catalog từ Spring MVC", r));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error(e.getMessage()));
        }
    }
}