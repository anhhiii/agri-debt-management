package com.manage.debt_management.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manage.debt_management.dto.CustomerContractSummaryDTO;
import com.manage.debt_management.dto.CustomerDetailResponseDTO;
import com.manage.debt_management.dto.CustomerRequestDTO;
import com.manage.debt_management.exception.ConflictException;
import com.manage.debt_management.facade.CustomerValidationFacade;
import com.manage.debt_management.model.ContractItem;
import com.manage.debt_management.model.Customer;
import com.manage.debt_management.model.InstallmentContract;
import com.manage.debt_management.repository.CustomerRepository;
import com.manage.debt_management.repository.InstallmentContractRepository;
import com.manage.debt_management.util.ContractLoanCalculator;
import com.manage.debt_management.util.ContractLoanCalculator.LoanDetailsResult;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InstallmentContractRepository installmentContractRepository;

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
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(String id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Khách hàng không tồn tại"));
    }

    /**
     * Khách + hợp đồng + tổng dư nợ/lãi (một lần gọi cho trang chi tiết).
     */
    public CustomerDetailResponseDTO getCustomerDetail(String id) {
        Customer customer = getCustomerById(id);
        List<InstallmentContract> contractList = installmentContractRepository
                .findAllByCustomerIdOrderByStartDateDesc(id);

        BigDecimal sumDebt = BigDecimal.ZERO;
        BigDecimal sumInterest = BigDecimal.ZERO;
        List<CustomerContractSummaryDTO> summaries = new ArrayList<>();

        for (InstallmentContract c : contractList) {
            LoanDetailsResult calc = ContractLoanCalculator.calculate(c);
            sumDebt = sumDebt.add(calc.getRemainingAmount());
            sumInterest = sumInterest.add(calc.getTotalInterest());
            summaries.add(toContractSummary(c, calc));
        }

        return CustomerDetailResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .farmingLocation(customer.getFarmingLocation())
                .customerTier(customer.getCustomerTier())
                .lastPurchaseDate(customer.getLastPurchaseDate())
                .totalCurrentDebt(customer.getTotalCurrentDebt())
                .lifetimePurchaseValue(customer.getLifetimePurchaseValue())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .totalRemainingDebt(sumDebt)
                .totalExpectedInterest(sumInterest)
                .contracts(summaries)
                .build();
    }

    private static CustomerContractSummaryDTO toContractSummary(InstallmentContract c, LoanDetailsResult calc) {
        return CustomerContractSummaryDTO.builder()
                .id(c.getId())
                .productName(productNameLabel(c))
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .principal(nz(calc.getInitialPrincipal()))
                .totalInterest(calc.getTotalInterest())
                .remainingAmount(calc.getRemainingAmount())
                .loanStatus(calc.getLoanStatus())
                .build();
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private static String productNameLabel(InstallmentContract c) {
        if (c.getItems() == null || c.getItems().isEmpty()) {
            return "";
        }
        return c.getItems().stream()
                .map(ContractItem::getProductName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" · "));
    }

    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.findByNameContainingIgnoreCaseOrPhoneContaining(keyword, keyword);
    }

    public Customer updateCustomer(String id, CustomerRequestDTO request) {
        customerValidationFacade.validateUpdate(id, request);      
        Customer existingCustomer = getCustomerById(id);
        if (!existingCustomer.getPhone().equals(request.getPhone())) {
            Optional<Customer> phoneCheck = customerRepository.findByPhone(request.getPhone());
            if (phoneCheck.isPresent()) {
                throw new ConflictException("Số điện thoại đã tồn tại cho một khách hàng khác", "phone", request.getPhone());
            }
        }
        existingCustomer.setName(request.getName());
        existingCustomer.setPhone(request.getPhone());
        existingCustomer.setAddress(request.getAddress());
        existingCustomer.setFarmingLocation(request.getFarmingLocation());     
        if (request.getCustomerTier() != null) {
            existingCustomer.setCustomerTier(request.getCustomerTier());
        } 
        existingCustomer.setUpdatedAt(LocalDateTime.now());
        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(String id) {
        Customer existingCustomer = getCustomerById(id);
        customerRepository.delete(existingCustomer);
    }
}