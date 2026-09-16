package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByInventoryId(Long inventoryId);
    List<InventoryItem> findByDiscrepancyNot(Integer discrepancy);
}
