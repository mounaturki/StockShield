package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.ProductPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPeriodRepository extends JpaRepository<ProductPeriod, Long> {
    Optional<ProductPeriod> findByActiveTrue();
    Optional<ProductPeriod> findByCode(String code);
}
