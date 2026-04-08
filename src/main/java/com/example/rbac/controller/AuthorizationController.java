package com.example.rbac.controller;

import com.example.rbac.dto.RbacDtos;
import com.example.rbac.model.Permission;
import com.example.rbac.model.Role;
import com.example.rbac.service.AuthorizationService;
import com.example.rbac.service.RbacAdministrationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthorizationController {
    private final AuthorizationService authorizationService;
    private final RbacAdministrationService administrationService;

    public AuthorizationController(AuthorizationService authorizationService, RbacAdministrationService administrationService) {
        this.authorizationService = authorizationService;
        this.administrationService = administrationService;
    }

    @PostMapping("/authorize")
    public RbacDtos.AuthorizeResponse authorize(@Valid @RequestBody RbacDtos.AuthorizeRequest req) {
        return authorizationService.authorize(req);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Role> roles() {
        return administrationService.getRoles();
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public Role createRole(@Valid @RequestBody RbacDtos.CreateRoleRequest req) {
        return administrationService.createRole(req);
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public Permission createPermission(@Valid @RequestBody RbacDtos.CreatePermissionRequest req) {
        return administrationService.createPermission(req);
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public void assignRole(@Valid @RequestBody RbacDtos.AssignRoleRequest req) {
        administrationService.assignRoleToUser(req);
    }
}
