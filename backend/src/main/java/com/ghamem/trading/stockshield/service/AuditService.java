package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.entity.AuditLog;
import com.ghamem.trading.stockshield.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(Long userId, String username, String action, String entityType,
                    Long entityId, String oldValue, String newValue, String ipAddress) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(log);
    }
}
