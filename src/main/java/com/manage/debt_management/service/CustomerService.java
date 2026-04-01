package com.manage.debt_management.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manage.debt_management.dto.CustomerRequestDTO;
import com.manage.debt_management.facade.CustomerValidationFacade;
import com.manage.debt_management.model.Customer;
import com.manage.debt_management.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerValidationFacade customerValidationFacade;

    public Customer createCustomer(CustomerRequestDTO customer) {
        customerValidationFacade.validateCreate(customer);
        Customer newCustomer = Customer.builder()
            .phone(customer.getPhone())
            .name(customer.getName())
            .address(customer.getAddress())
            .farmingLocation(customer.getFarmingLocation())
            .customerTier(customer.getCustomerTier())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        return customerRepository.save(newCustomer);
    }
}