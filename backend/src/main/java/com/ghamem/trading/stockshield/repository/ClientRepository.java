package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByActiveTrue();
    List<Client> findByContractExpiryBefore(LocalDate date);
}
