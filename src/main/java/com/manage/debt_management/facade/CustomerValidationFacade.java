package com.manage.debt_management.facade;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.manage.debt_management.dto.CustomerRequestDTO;
import com.manage.debt_management.exception.ConflictException;
import com.manage.debt_management.exception.ValidationException;
import com.manage.debt_management.shared.CustomerServiceShared;

@Component
public class CustomerValidationFacade {

    @Autowired
    private CustomerServiceShared customerServiceShared;

    public void validateCreate(CustomerRequestDTO customer) {
        Map<String, String> errors = new HashMap<>();
        if (customer == null) {
            errors.put("body", "Body không được để trống");
            throw new ValidationException("Dữ liệu không hợp lệ", errors);
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            errors.put("phone", "Thiếu số điện thoại");
        }
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            errors.put("name", "Thiếu tên khách hàng");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Dữ liệu không hợp lệ", errors);
        }

        if (customerServiceShared.isCustomerExist(customer.getPhone())) {
            throw new ConflictException("Số điện thoại đã tồn tại", "phone", customer.getPhone());
        }
    }

    public void validateUpdate(String id, CustomerRequestDTO customer) {
        Map<String, String> errors = new HashMap<>();
        if (id == null || id.trim().isEmpty()) {
            errors.put("id", "ID khách hàng không được để trống");
        }
        if (customer == null) {
            errors.put("body", "Body không được để trống");
            throw new ValidationException("Dữ liệu không hợp lệ", errors);
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            errors.put("phone", "Thiếu số điện thoại");
        }
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            errors.put("name", "Thiếu tên khách hàng");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Dữ liệu không hợp lệ", errors);
        }
    }
}

