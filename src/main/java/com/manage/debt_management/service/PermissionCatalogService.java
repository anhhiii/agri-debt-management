package com.manage.debt_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manage.debt_management.dto.PermissionSummaryDTO;
import com.manage.debt_management.repository.PermissionRepository;

/**
 * Catalog permission (sync endpoint) — chỉ đọc collection {@code permissions}.
 */
@Service
public class PermissionCatalogService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<PermissionSummaryDTO> findAllForCatalog() {
        return permissionRepository.findAllByOrderByModuleAscCodeAsc().stream()
                .map(PermissionSummaryDTO::from)
                .collect(Collectors.toList());
    }
}
