package com.manage.debt_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.debt_management.service.InstallmentContractService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private InstallmentContractService contractService;

    @PostMapping("/collect")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<com.manage.debt_management.dto.ResponseApi<?>> collectPayment(
            @RequestBody com.manage.debt_management.dto.PaymentRequestDTO req) {
        try {
        	// 1. Lấy identity của người đang đăng nhập
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            String currentUserName = auth.getName(); 
            System.out.print(currentUserName);
            
            // 2. Gán cứng vào DTO. Dù Frontend có gửi gì lên thì cũng bị ghi đè bởi User đang login
            req.setReceivedBy(currentUserName);
            

            return ResponseEntity.ok(
                    com.manage.debt_management.dto.ResponseApi.ok("Ghi nhận trả tiền thành công", 
                    contractService.recordPayment(req)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(com.manage.debt_management.dto.ResponseApi.error(e.getMessage()));
        }
    }
}