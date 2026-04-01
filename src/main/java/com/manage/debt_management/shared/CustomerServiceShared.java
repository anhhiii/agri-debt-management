package com.manage.debt_management.shared;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manage.debt_management.repository.CustomerRepository;

@Service
public class CustomerServiceShared {

    @Autowired
    private CustomerRepository customerRepository;

    public boolean isCustomerExist(String phone) {
        return customerRepository.findByPhone(phone).isPresent();
    }
}