package com.manage.debt_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manage.debt_management.model.Permission;

@Repository
public interface PermissionRepository extends MongoRepository<Permission, String> {

    Optional<Permission> findByCode(String code);

    List<Permission> findAllByOrderByModuleAscCodeAsc();
}
