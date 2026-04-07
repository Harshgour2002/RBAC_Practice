package com.example.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class RbacDtos {
    public record AuthorizeRequest(@NotNull Long userId, @NotBlank String permission) {}
    public record AuthorizeResponse(boolean allowed) {}
    public record CreateRoleRequest(@NotBlank String name) {}
    public record CreatePermissionRequest(@NotBlank String name) {}
    public record AssignRoleRequest(@NotNull Long userId, @NotBlank String roleName) {}
    public record AssignPermissionToRoleRequest(@NotBlank String roleName, Set<String> permissions) {}
}
