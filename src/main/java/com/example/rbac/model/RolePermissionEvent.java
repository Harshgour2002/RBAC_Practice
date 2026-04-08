package com.example.rbac.model;

import lombok.*;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RolePermissionEvent {
    private Long userId;
    private RolePermissionEventType eventType;
    private Instant occurredAt;
}
