package com.manage.debt_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.manage.debt_management.dto.CustomerDetailResponseDTO;
import com.manage.debt_management.dto.CustomerRequestDTO;
import com.manage.debt_management.dto.CustomerResponseDTO;
import com.manage.debt_management.dto.ResponseApi;
import com.manage.debt_management.facade.CustomerErrorFacade;
import com.manage.debt_management.model.Customer;
import com.manage.debt_management.service.CustomerService;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerErrorFacade customerErrorFacade;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseApi<?>> createCustomer(@RequestBody CustomerRequestDTO customer) {
        try {
            Customer created = customerService.createCustomer(customer);
            CustomerResponseDTO response = CustomerResponseDTO.builder()
                .id(created.getId())
                .name(created.getName())
                .phone(created.getPhone())
                .address(created.getAddress())
                .farmingLocation(created.getFarmingLocation())
                .customerTier(created.getCustomerTier())
                .lastPurchaseDate(created.getLastPurchaseDate())
                .totalCurrentDebt(created.getTotalCurrentDebt())
                .lifetimePurchaseValue(created.getLifetimePurchaseValue())
                .createdAt(created.getCreatedAt())
                .updatedAt(created.getUpdatedAt())
                .build();
            return ResponseEntity.ok(ResponseApi.ok("Tạo khách hàng thành công", response));
        } catch (Exception e) {
            return customerErrorFacade.handleCreateCustomer(e);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseApi<?>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(ResponseApi.ok("Lấy danh sách khách hàng thành công", customers));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseApi.error("Lỗi khi lấy danh sách khách hàng: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseApi<?>> getCustomerById(@PathVariable String id) {
        try {
            CustomerDetailResponseDTO detail = customerService.getCustomerDetail(id);
            return ResponseEntity.ok(ResponseApi.ok("Lấy thông tin khách hàng thành công", detail));
        } catch (Exception e) {
            return customerErrorFacade.handleUpdateCustomer(e); // Can reuse same error facade logic for 404
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseApi<?>> searchCustomers(@RequestParam String keyword) {
        try {
            List<Customer> customers = customerService.searchCustomers(keyword);
            return ResponseEntity.ok(ResponseApi.ok("Tìm kiếm khách hàng thành công", customers));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseApi.error("Lỗi khi tìm kiếm khách hàng: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseApi<?>> updateCustomer(
            @PathVariable String id, 
            @RequestBody CustomerRequestDTO customer) {
        try {
            Customer updated = customerService.updateCustomer(id, customer);
            CustomerResponseDTO response = CustomerResponseDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .phone(updated.getPhone())
                .address(updated.getAddress())
                .farmingLocation(updated.getFarmingLocation())
                .customerTier(updated.getCustomerTier())
                .lastPurchaseDate(updated.getLastPurchaseDate())
                .totalCurrentDebt(updated.getTotalCurrentDebt())
                .lifetimePurchaseValue(updated.getLifetimePurchaseValue())
                .createdAt(updated.getCreatedAt())
                .updatedAt(updated.getUpdatedAt())
                .build();
                
            return ResponseEntity.ok(ResponseApi.ok("Cập nhật khách hàng thành công", response));
        } catch (Exception e) {
            return customerErrorFacade.handleUpdateCustomer(e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseApi<?>> deleteCustomer(@PathVariable String id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(ResponseApi.ok("Xóa khách hàng thành công", null));
        } catch (Exception e) {
            return customerErrorFacade.handleUpdateCustomer(e);
        }
    }

}