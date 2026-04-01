package com.manage.debt_management.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import com.manage.debt_management.model.UserAccount;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
    long count();
    Optional<UserAccount> findById(String id);
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByPhone(String phone);
}