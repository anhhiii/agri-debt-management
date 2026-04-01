package com.manage.debt_management.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manage.debt_management.model.Customer;


public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByPhone(String phone);
}