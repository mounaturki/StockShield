package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.*;
import com.ghamem.trading.stockshield.entity.*;
import com.ghamem.trading.stockshield.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ClientRepository clientRepository;
    private final MachineRepository machineRepository;
    private final ProductRepository productRepository;
    private final AlertRepository alertRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Transactional(readOnly = true)
    public DashboardDto getDashboard() {
        List<Alert> recentAlerts = alertRepository.findByResolvedFalseOrderByCreatedAtDesc();
        List<AlertDto> alertDtos = recentAlerts.stream().limit(5).map(this::toAlertDto).toList();

        return DashboardDto.builder()
                .totalClients(clientRepository.count())
                .totalMachines(machineRepository.count())
                .totalProducts(productRepository.count())
                .activeAlerts(alertRepository.countByResolvedFalse())
                .failedLogins(loginHistoryRepository.countBySuccessFalseAndCreatedAtAfter(
                        LocalDateTime.now().minusHours(24)))
                .lowStockProducts(productRepository.findLowStockProducts().size())
                .inventoryDiscrepancies(inventoryItemRepository.findByDiscrepancyNot(0).size())
                .recentAlerts(alertDtos)
                .build();
    }

    public RiskCenterDto getRiskCenter() {
        List<RiskItemDto> riskyUsers = new ArrayList<>();
        loginHistoryRepository.findBySuccessFalseAndCreatedAtAfter(LocalDateTime.now().minusHours(24))
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(LoginHistory::getUsername, java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() >= 3)
                .forEach(e -> riskyUsers.add(RiskItemDto.builder()
                        .title(e.getKey())
                        .description(e.getValue() + " tentatives échouées")
                        .severity("CRITICAL")
                        .entityType("User")
                        .build()));

        List<RiskItemDto> criticalProducts = productRepository.findLowStockProducts().stream()
                .map(p -> RiskItemDto.builder()
                        .id(p.getId())
                        .title(p.getName())
                        .description("Stock: " + p.getCurrentStock() + " / Min: " + p.getMinimumStock())
                        .severity("HIGH")
                        .entityType("Product")
                        .entityId(p.getId())
                        .build())
                .toList();

        List<RiskItemDto> maintenanceMachines = machineRepository
                .findByNextMaintenanceBefore(LocalDate.now().plusDays(14)).stream()
                .map(m -> RiskItemDto.builder()
                        .id(m.getId())
                        .title(m.getSerialNumber())
                        .description("Maintenance prévue: " + m.getNextMaintenance())
                        .severity("MEDIUM")
                        .entityType("Machine")
                        .entityId(m.getId())
                        .build())
                .toList();

        List<RiskItemDto> expiringContracts = clientRepository
                .findByContractExpiryBefore(LocalDate.now().plusDays(30)).stream()
                .map(c -> RiskItemDto.builder()
                        .id(c.getId())
                        .title(c.getName())
                        .description("Expire le: " + c.getContractExpiry())
                        .severity(c.getContractExpiry().isBefore(LocalDate.now()) ? "CRITICAL" : "MEDIUM")
                        .entityType("Client")
                        .entityId(c.getId())
                        .build())
                .toList();

        List<RiskItemDto> inventoryAnomalies = inventoryItemRepository.findByDiscrepancyNot(0).stream()
                .map(item -> RiskItemDto.builder()
                        .id(item.getId())
                        .title(item.getProduct().getName())
                        .description("Écart: " + item.getDiscrepancy())
                        .severity(Math.abs(item.getDiscrepancy()) > 10 ? "HIGH" : "MEDIUM")
                        .entityType("InventoryItem")
                        .entityId(item.getId())
                        .build())
                .toList();

        return RiskCenterDto.builder()
                .riskyUsers(riskyUsers)
                .criticalProducts(criticalProducts)
                .maintenanceMachines(maintenanceMachines)
                .expiringContracts(expiringContracts)
                .inventoryAnomalies(inventoryAnomalies)
                .build();
    }

    public StockRiskDto getStockRisk() {
        List<RiskItemDto> criticalProducts = productRepository.findLowStockProducts().stream()
                .map(p -> RiskItemDto.builder()
                        .id(p.getId())
                        .title(p.getName())
                        .description("Stock: " + p.getCurrentStock() + " / Min: " + p.getMinimumStock()
                                + (p.getCategory() != null ? " — " + p.getCategory().getLabel() : ""))
                        .severity("HIGH")
                        .entityType("Product")
                        .entityId(p.getId())
                        .build())
                .toList();

        List<RiskItemDto> inventoryAnomalies = inventoryItemRepository.findByDiscrepancyNot(0).stream()
                .map(item -> RiskItemDto.builder()
                        .id(item.getId())
                        .title(item.getProduct().getName())
                        .description("Écart: " + item.getDiscrepancy())
                        .severity(Math.abs(item.getDiscrepancy()) > 10 ? "HIGH" : "MEDIUM")
                        .entityType("InventoryItem")
                        .entityId(item.getId())
                        .build())
                .toList();

        List<RiskItemDto> stockAlerts = alertRepository.findByResolvedFalseOrderByCreatedAtDesc().stream()
                .filter(a -> a.getType() == AlertType.LOW_STOCK
                        || a.getType() == AlertType.INVENTORY_DISCREPANCY
                        || a.getType() == AlertType.SUSPICIOUS_ACTIVITY)
                .map(a -> RiskItemDto.builder()
                        .id(a.getId())
                        .title(a.getTitle())
                        .description(a.getMessage())
                        .severity(a.getSeverity().name())
                        .entityType(a.getEntityType())
                        .entityId(a.getEntityId())
                        .build())
                .toList();

        return StockRiskDto.builder()
                .criticalProducts(criticalProducts)
                .inventoryAnomalies(inventoryAnomalies)
                .stockAlerts(stockAlerts)
                .lowStockCount(criticalProducts.size())
                .discrepancyCount(inventoryAnomalies.size())
                .build();
    }

    private AlertDto toAlertDto(Alert alert) {
        return AlertDto.builder()
                .id(alert.getId())
                .type(alert.getType())
                .severity(alert.getSeverity())
                .title(alert.getTitle())
                .message(alert.getMessage())
                .entityType(alert.getEntityType())
                .entityId(alert.getEntityId())
                .read(alert.isRead())
                .resolved(alert.isResolved())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
