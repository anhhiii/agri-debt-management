package com.manage.debt_management.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manage.debt_management.dto.RoleCreateRequestDTO;
import com.manage.debt_management.dto.RoleResponseDTO;
import com.manage.debt_management.dto.RoleUpdateRequestDTO;
import com.manage.debt_management.model.Role;
import com.manage.debt_management.repository.PermissionRepository;
import com.manage.debt_management.repository.RoleRepository;
import com.manage.debt_management.repository.UserAccountRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    public List<RoleResponseDTO> findAll() {
        return roleRepository.findAll().stream().map(RoleResponseDTO::from).collect(Collectors.toList());
    }

    public RoleResponseDTO findById(String id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò"));
        return RoleResponseDTO.from(role);
    }

    public RoleResponseDTO create(RoleCreateRequestDTO req) {
        if (req.getName() == null) {
            throw new IllegalArgumentException("Thiếu tên vai trò (name)");
        }
        if (roleRepository.existsByName(req.getName())) {
            throw new IllegalArgumentException("Vai trò đã tồn tại: " + req.getName());
        }
        Role role = new Role();
        role.setName(req.getName());
        role.setDescription(req.getDescription());
        role.setPermissions(resolveAndValidateCodes(req.getPermissionCodes()));
        Role saved = roleRepository.save(role);
        return RoleResponseDTO.from(saved);
    }

    public RoleResponseDTO update(String id, RoleUpdateRequestDTO req) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò"));
        if (req.getDescription() != null) {
            role.setDescription(req.getDescription());
        }
        if (req.getPermissionCodes() != null) {
            role.setPermissions(resolveAndValidateCodes(req.getPermissionCodes()));
        }
        Role saved = roleRepository.save(role);
        return RoleResponseDTO.from(saved);
    }

    public void deleteById(String id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò"));
        if (userAccountRepository.existsByRole_Id(role.getId())) {
            throw new IllegalArgumentException("Không xóa được: còn tài khoản gắn vai trò này");
        }
        roleRepository.delete(role);
    }

    /**
     * Mỗi code phải tồn tại trong catalog {@code permissions}. Trả về danh sách chuỗi (giữ thứ tự, bỏ trùng).
     */
    private List<String> resolveAndValidateCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> ordered = new LinkedHashSet<>();
        for (String c : codes) {
            if (c == null) {
                continue;
            }
            String t = c.trim();
            if (t.isEmpty()) {
                continue;
            }
            permissionRepository.findByCode(t)
                    .orElseThrow(() -> new IllegalArgumentException("Permission không có trong catalog: " + t));
            ordered.add(t);
        }
        return new ArrayList<>(ordered);
    }
}
