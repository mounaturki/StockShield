package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.Alert;
import com.ghamem.trading.stockshield.entity.AlertSeverity;
import com.ghamem.trading.stockshield.entity.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByResolvedFalseOrderByCreatedAtDesc();
    List<Alert> findBySeverity(AlertSeverity severity);
    List<Alert> findByType(AlertType type);
    long countByResolvedFalse();
}
