package com.manage.debt_management.controller;

import com.manage.debt_management.dto.DashboardOverviewDTO;
import com.manage.debt_management.dto.ResponseApi;
import com.manage.debt_management.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseApi<DashboardOverviewDTO>> stats() {
        try {
            DashboardOverviewDTO data = dashboardService.getOverviewStats();
            return ResponseEntity.ok(ResponseApi.ok("Lấy số liệu tổng quan thành công", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseApi.error("Lỗi lấy số liệu: " + e.getMessage()));
        }
    }
    
}