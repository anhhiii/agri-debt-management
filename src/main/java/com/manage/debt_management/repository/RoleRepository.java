package com.manage.debt_management.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manage.debt_management.enums.ERole;
import com.manage.debt_management.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole role);
    boolean existsByName(ERole role);
    long count();
    Optional<Role> findById(String id);
}