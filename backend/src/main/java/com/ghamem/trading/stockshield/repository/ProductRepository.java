package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    List<Product> findByCategory(com.ghamem.trading.stockshield.entity.ProductCategory category);
    List<Product> findByPeriodCodeAndActiveTrue(String periodCode);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.currentStock <= p.minimumStock")
    List<Product> findLowStockProducts();
}
