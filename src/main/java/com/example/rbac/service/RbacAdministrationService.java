package com.example.rbac.service;

import com.example.rbac.audit.AuditService;
import com.example.rbac.dto.RbacDtos;
import com.example.rbac.events.RbacEventPublisher;
import com.example.rbac.exception.BadRequestException;
import com.example.rbac.model.*;
import com.example.rbac.repository.PermissionRepository;
import com.example.rbac.repository.RoleRepository;
import com.example.rbac.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RbacAdministrationService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RbacEventPublisher eventPublisher;
    private final AuditService auditService;

    public RbacAdministrationService(RoleRepository roleRepository, PermissionRepository permissionRepository,
                                     UserRepository userRepository, RbacEventPublisher eventPublisher,
                                     AuditService auditService) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.auditService = auditService;
    }

    public List<Role> getRoles() { return roleRepository.findAll(); }

    public Role createRole(RbacDtos.CreateRoleRequest req) {
        return roleRepository.save(Role.builder().name(req.name().toUpperCase()).build());
    }

    public Permission createPermission(RbacDtos.CreatePermissionRequest req) {
        Permission permission = permissionRepository.save(Permission.builder().name(req.name().toUpperCase()).build());
        auditService.log(null, AuditEventType.PERMISSION_UPDATED, "Permission created: " + req.name());
        return permission;
    }

    @Transactional
    public void assignRoleToUser(RbacDtos.AssignRoleRequest req) {
        User user = userRepository.findById(req.userId()).orElseThrow(() -> new BadRequestException("User not found"));
        Role role = roleRepository.findByName(req.roleName().toUpperCase()).orElseThrow(() -> new BadRequestException("Role not found"));
        user.getRoles().add(role);
        auditService.log(user.getId(), AuditEventType.ROLE_ASSIGNED, "Role assigned: " + req.roleName());
        eventPublisher.publish(RolePermissionEvent.builder().userId(user.getId()).eventType(RolePermissionEventType.USER_ROLE_ASSIGNMENT_CHANGED).occurredAt(Instant.now()).build());
    }

    @Transactional
    public void assignPermissionsToRole(RbacDtos.AssignPermissionToRoleRequest req) {
        Role role = roleRepository.findByName(req.roleName().toUpperCase()).orElseThrow(() -> new BadRequestException("Role not found"));
        req.permissions().forEach(p -> {
            Permission permission = permissionRepository.findByName(p.toUpperCase())
                    .orElseGet(() -> permissionRepository.save(Permission.builder().name(p.toUpperCase()).build()));
            role.getPermissions().add(permission);
        });
    }
}
