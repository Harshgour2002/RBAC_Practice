package com.example.rbac.events;

import com.example.rbac.caching.PermissionCacheService;
import com.example.rbac.model.RolePermissionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RbacEventConsumer {
    private final PermissionCacheService permissionCacheService;

    public RbacEventConsumer(PermissionCacheService permissionCacheService) {
        this.permissionCacheService = permissionCacheService;
    }

    @KafkaListener(topics = "rbac.events", groupId = "rbac-authz")
    public void onEvent(RolePermissionEvent event) {
        permissionCacheService.invalidate(event.getUserId());
        permissionCacheService.computeAndWarm(event.getUserId());
    }
}
