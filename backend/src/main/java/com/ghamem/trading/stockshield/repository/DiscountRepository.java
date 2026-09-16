package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByActiveTrue();
    List<Discount> findByValidatedFalse();
}
