package com.manage.debt_management.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manage.debt_management.enums.ERole;
import com.manage.debt_management.model.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByName(ERole role);

    boolean existsByName(ERole role);
}
