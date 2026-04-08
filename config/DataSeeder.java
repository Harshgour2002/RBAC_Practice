package com.example.CVRUK_backend.authentication.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.CVRUK_backend.authentication.entity.permission;
import com.example.CVRUK_backend.authentication.entity.role;
import com.example.CVRUK_backend.authentication.enums.enum_permission;
import com.example.CVRUK_backend.authentication.enums.enum_roleName;
import com.example.CVRUK_backend.authentication.repository.permissionRepo;
import com.example.CVRUK_backend.authentication.repository.roleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final roleRepo roleRepository;
    private final permissionRepo permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {

        // ✅ create permissions
        permission userView = createPermission(
                enum_permission.PUBLIC_VIEW_CONTENT.name(),
                "Can view user resources"
        );

        permission adminDashboard = createPermission(
                enum_permission.ADMIN_VIEW_ALL.name(),
                "Can access admin dashboard"
        );

        // ✅ create roles
        role userRole = createRole(
                enum_roleName.USER.name(),
                "Default public user role"
        );

        role adminRole = createRole(
                enum_roleName.ADMIN.name(),
                "System administrator role"
        );

        createRole(enum_roleName.STUDENT.name(), "Student role placeholder");
        createRole(enum_roleName.FACULTY.name(), "Faculty role placeholder");

        // 🔥 ENTERPRISE WAY — mutate existing collection
        userRole.getPermissions().clear();
        userRole.getPermissions().add(userView);

        adminRole.getPermissions().clear();
        adminRole.getPermissions().add(userView);
        adminRole.getPermissions().add(adminDashboard);

        roleRepository.save(userRole);
        roleRepository.save(adminRole);

        log.info("RBAC seed complete");
    }

    private permission createPermission(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(
                        permission.builder()
                                .name(name)
                                .description(description)
                                .build()
                ));
    }

    private role createRole(String name, String description) {
        return roleRepository.getByName(name)
                .orElseGet(() -> roleRepository.save(
                        role.builder()
                                .name(name)
                                .description(description)
                                .build()
                ));
    }
}
