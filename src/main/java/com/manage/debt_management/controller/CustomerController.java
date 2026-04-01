package com.manage.debt_management.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}