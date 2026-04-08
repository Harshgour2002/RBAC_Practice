package com.example.rbac.service;

import com.example.rbac.caching.PermissionCacheService;
import com.example.rbac.dto.RbacDtos;
import com.example.rbac.metrics.SecurityMetrics;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    private final PermissionCacheService permissionCacheService;
    private final SecurityMetrics securityMetrics;

    public AuthorizationService(PermissionCacheService permissionCacheService, SecurityMetrics securityMetrics) {
        this.permissionCacheService = permissionCacheService;
        this.securityMetrics = securityMetrics;
    }

    public RbacDtos.AuthorizeResponse authorize(RbacDtos.AuthorizeRequest req) {
        return securityMetrics.authorizationLatency.record(() -> {
            try {
                boolean allowed = permissionCacheService.getPermissions(req.userId()).join()
                        .contains(req.permission().toUpperCase());
                return new RbacDtos.AuthorizeResponse(allowed);
            } catch (Exception e) {
                securityMetrics.authErrors.increment();
                return new RbacDtos.AuthorizeResponse(false);
            }
        });
    }
}
