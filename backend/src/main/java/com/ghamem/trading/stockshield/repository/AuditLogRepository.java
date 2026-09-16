package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
    List<AuditLog> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);
}
