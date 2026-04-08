package com.example.rbac.audit;

import com.example.rbac.model.AuditEventType;
import com.example.rbac.model.AuditLog;
import com.example.rbac.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Long userId, AuditEventType type, String details) {
        auditLogRepository.save(AuditLog.builder()
                .userId(userId)
                .eventType(type)
                .details(details)
                .createdAt(Instant.now())
                .build());
    }
}
