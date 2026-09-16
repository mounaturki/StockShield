package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUsernameOrderByCreatedAtDesc(String username);
    List<LoginHistory> findBySuccessFalseAndCreatedAtAfter(LocalDateTime date);
    long countBySuccessFalseAndCreatedAtAfter(LocalDateTime date);
}
