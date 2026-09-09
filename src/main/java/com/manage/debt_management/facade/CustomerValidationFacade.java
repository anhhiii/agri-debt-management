package com.manage.debt_management.facade;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.manage.debt_management.dto.CustomerRequestDTO;
import com.manage.debt_management.exception.ConflictException;
import com.manage.debt_management.exception.ValidationException;
import com.manage.debt_management.shared.CustomerServiceShared;

@Component
public class CustomerValidationFacade {

    // SĐT Việt Nam hợp lệ: 10 số, bắt đầu bằng 03/05/07/08/09
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0[35789]\\d{8})$");

    @Autowired
    private CustomerServiceShared customerServiceShared;

    public void validateCreate(CustomerRequestDTO customer) {
        Map<String, String> errors = new HashMap<>();

        if (customer == null) {
            errors.put("body", "Body không được để trống");
            throw new ValidationException("Dữ liệu không hợp lệ", errors);
        }

        validateRequiredFields(customer, errors);

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

        validateRequiredFields(customer, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Dữ liệu không hợp lệ", errors);
        }
    }

    /**
     * Validate các trường bắt buộc chung cho cả create và update.
     * Đây là lớp bảo vệ thứ 2, sau Bean Validation (@Valid ở controller).
     */
    private void validateRequiredFields(CustomerRequestDTO customer, Map<String, String> errors) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            errors.put("name", "Thiếu họ tên");
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            errors.put("phone", "Thiếu số điện thoại");
        } else if (!PHONE_PATTERN.matcher(customer.getPhone().trim()).matches()) {
            errors.put("phone", "Số điện thoại phải có 10 số và bắt đầu bằng 03/05/07/08/09");
        }
        if (customer.getAddress() == null || customer.getAddress().trim().isEmpty()) {
            errors.put("address", "Thiếu địa chỉ");
        }
        if (customer.getFarmingLocation() == null || customer.getFarmingLocation().trim().isEmpty()) {
            errors.put("farmingLocation", "Thiếu vùng canh tác");
        }
    }
}
