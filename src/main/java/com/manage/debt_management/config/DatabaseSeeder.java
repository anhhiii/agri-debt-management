package com.manage.debt_management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.manage.debt_management.enums.ERole;
import com.manage.debt_management.model.Role;
import com.manage.debt_management.model.UserAccount;
import com.manage.debt_management.repository.RoleRepository;
import com.manage.debt_management.repository.UserAccountRepository;

import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Khởi tạo Role ADMIN nếu chưa có
        if (roleRepository.findByName(ERole.ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(ERole.ADMIN);
            adminRole.setDescription("Chủ đại lý (Toàn quyền)");
            // Cấp tất cả các quyền cho Chủ đại lý
            adminRole.setPermissions(Arrays.asList("/api/v1/roles:POST", "/api/v1/users:POST", "/api/v1/permissions:POST"));
            
            roleRepository.save(adminRole);
            System.out.println("Đã tạo Role: ROLE_ADMIN");
        }

        // 2. Khởi tạo Role STAFF nếu chưa có
        if (roleRepository.findByName(ERole.STAFF).isEmpty()) {
            Role staffRole = new Role();
            staffRole.setName(ERole.STAFF);
            staffRole.setDescription("Nhân viên bán hàng");
            // Nhân viên chỉ được tạo khách, tạo nợ, xem nợ (KHÔNG được xóa nợ, KHÔNG xem báo cáo)
            staffRole.setPermissions(Arrays.asList(
                    "/api/v1/roles:POST", "/api/v1/users:POST", "/api/v1/debt:POST"
            ));
            roleRepository.save(staffRole);
            System.out.println("Đã tạo Role: ROLE_STAFF");
        }

        // 3. Khởi tạo User ADMIN mặc định
        if (userAccountRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName(ERole.ADMIN).get();
        
            UserAccount adminUser = new UserAccount();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("123456")); // Tạm thời để plain text
            adminUser.setEmail("admin@example.com");
            adminUser.setPhone("1234567890");
            adminUser.setRole(adminRole);
            userAccountRepository.save(adminUser);
            System.out.println("Đã tạo User mặc định: admin/123456");
        }
    }
}