package com.manage.debt_management.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.manage.debt_management.enums.ERole;
import com.manage.debt_management.model.Permission;
import com.manage.debt_management.model.Role;
import com.manage.debt_management.model.UserAccount;
import com.manage.debt_management.repository.PermissionRepository;
import com.manage.debt_management.repository.RoleRepository;
import com.manage.debt_management.repository.UserAccountRepository;
import com.manage.debt_management.security.ApiPermissions;
import com.manage.debt_management.service.PermissionCatalogSyncService;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedPermissionsIfEmpty();

        if (roleRepository.findByName(ERole.ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(ERole.ADMIN);
            adminRole.setDescription("Chủ đại lý (Toàn quyền)");
            adminRole.setPermissions(ApiPermissions.allCodesForAdmin());
            roleRepository.save(adminRole);
            System.out.println("Đã tạo Role: ADMIN");
        }

        if (roleRepository.findByName(ERole.STAFF).isEmpty()) {
            Role staffRole = new Role();
            staffRole.setName(ERole.STAFF);
            staffRole.setDescription("Nhân viên bán hàng");
            staffRole.setPermissions(ApiPermissions.defaultCodesForStaff());
            roleRepository.save(staffRole);
            System.out.println("Đã tạo Role: STAFF");
        }

        if (userAccountRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName(ERole.ADMIN).get();

            UserAccount adminUser = new UserAccount();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("123456"));
            adminUser.setEmail("admin@example.com");
            adminUser.setPhone("1234567890");
            adminUser.setRole(adminRole);
            userAccountRepository.save(adminUser);
            System.out.println("Đã tạo User mặc định: admin/123456");
        }
    }

    /** Ghi catalog permission ban đầu — chỉ khi collection rỗng (sync startup sẽ bổ sung thêm nếu có). */
    private void seedPermissionsIfEmpty() {
        if (permissionRepository.count() > 0) {
            return;
        }
        List<Permission> batch = ApiPermissions.allCodesForAdmin().stream()
                .map(code -> {
                    int colon = code.lastIndexOf(':');
                    String path = colon > 0 ? code.substring(0, colon) : code;
                    return Permission.builder()
                            .code(code)
                            .description("Catalog: " + code)
                            .module(PermissionCatalogSyncService.moduleFromApiPath(path))
                            .build();
                })
                .toList();
        permissionRepository.saveAll(batch);
        System.out.println("Đã seed permissions (catalog): " + batch.size());
    }
}