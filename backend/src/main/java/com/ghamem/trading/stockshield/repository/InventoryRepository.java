package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.Inventory;
import com.ghamem.trading.stockshield.entity.InventoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByType(InventoryType type);
    List<Inventory> findByStatus(String status);
}
