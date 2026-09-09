package com.manage.debt_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.debt_management.dto.ResponseApi;
import com.manage.debt_management.dto.RoleCreateRequestDTO;
import com.manage.debt_management.dto.RoleResponseDTO;
import com.manage.debt_management.dto.RoleUpdateRequestDTO;
import com.manage.debt_management.service.RoleService;

/**
 * CRUD vai trò (RBAC): mô tả + danh sách mã permission trùng catalog {@code permissions}.
 */
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).ROLES_GET)")
    public ResponseEntity<ResponseApi<List<RoleResponseDTO>>> list() {
        try {
            return ResponseEntity.ok(ResponseApi.ok("Danh sách vai trò", roleService.findAll()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).ROLES_ID_GET)")
    public ResponseEntity<ResponseApi<RoleResponseDTO>> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ResponseApi.ok("Chi tiết vai trò", roleService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).ROLES_POST)")
    public ResponseEntity<ResponseApi<RoleResponseDTO>> create(@RequestBody RoleCreateRequestDTO body) {
        try {
            return ResponseEntity.ok(ResponseApi.ok("Tạo vai trò thành công", roleService.create(body)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).ROLES_ID_PUT)")
    public ResponseEntity<ResponseApi<RoleResponseDTO>> update(
            @PathVariable String id,
            @RequestBody RoleUpdateRequestDTO body) {
        try {
            return ResponseEntity.ok(ResponseApi.ok("Cập nhật vai trò thành công", roleService.update(id, body)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).ROLES_ID_DELETE)")
    public ResponseEntity<ResponseApi<?>> delete(@PathVariable String id) {
        try {
            roleService.deleteById(id);
            return ResponseEntity.ok(ResponseApi.ok("Xóa vai trò thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error(e.getMessage()));
        }
    }
}