package com.ghamem.trading.stockshield.repository;

import com.ghamem.trading.stockshield.entity.Machine;
import com.ghamem.trading.stockshield.entity.MachineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    List<Machine> findByStatus(MachineStatus status);
    List<Machine> findByNextMaintenanceBefore(LocalDate date);
    List<Machine> findByClientId(Long clientId);
}
