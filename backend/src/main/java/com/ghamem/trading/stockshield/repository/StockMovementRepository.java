package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.StockMovement;
import com.ghamem.trading.stockshield.entity.StockMovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);
    List<StockMovement> findByType(StockMovementType type);
    List<StockMovement> findByCreatedAtAfter(LocalDateTime date);
    long countByTypeAndCreatedAtAfter(StockMovementType type, LocalDateTime date);
}
