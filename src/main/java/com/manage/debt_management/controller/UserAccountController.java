package com.manage.debt_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.debt_management.dto.ResponseApi;
import com.manage.debt_management.dto.UserAccountResponseDTO;
import com.manage.debt_management.service.UserAccountService;

@RestController
@RequestMapping("/api/v1/users")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).USERS_GET)")
    public ResponseEntity<ResponseApi<List<UserAccountResponseDTO>>> list() {
        try {
            return ResponseEntity.ok(ResponseApi.ok("Danh sách tài khoản", userAccountService.findAll()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.manage.debt_management.security.ApiPermissions).USERS_ID_DELETE)")
    public ResponseEntity<ResponseApi<?>> delete(@PathVariable String id) {
        try {
            userAccountService.deleteById(id);
            return ResponseEntity.ok(ResponseApi.ok("Xóa tài khoản thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error(e.getMessage()));
        }
    }
}