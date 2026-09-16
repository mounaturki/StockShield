package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.AuditLogDto;
import com.ghamem.trading.stockshield.entity.AuditLog;
import com.ghamem.trading.stockshield.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public List<AuditLogDto> findAll() {
        return auditLogRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<AuditLogDto> findRecent() {
        return auditLogRepository.findByCreatedAtAfterOrderByCreatedAtDesc(
                java.time.LocalDateTime.now().minusDays(7))
                .stream().map(this::toDto).toList();
    }

    private AuditLogDto toDto(AuditLog log) {
        return AuditLogDto.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .username(log.getUsername())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
