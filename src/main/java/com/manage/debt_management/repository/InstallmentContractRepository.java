package com.manage.debt_management.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manage.debt_management.enums.ContractStatus;
import com.manage.debt_management.model.InstallmentContract;

@Repository
public interface InstallmentContractRepository extends MongoRepository<InstallmentContract, String> {

    long countByStatus(ContractStatus status);

    Page<InstallmentContract> findByCustomerId(String customerId, Pageable pageable);

    List<InstallmentContract> findAllByCustomerIdOrderByStartDateDesc(String customerId);
}