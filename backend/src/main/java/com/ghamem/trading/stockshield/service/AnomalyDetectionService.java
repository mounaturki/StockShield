package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.entity.*;
import com.ghamem.trading.stockshield.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnomalyDetectionService {

    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final MachineRepository machineRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final AlertRepository alertRepository;

    @Scheduled(fixedRate = 300000)
    public void detectAnomalies() {
        detectLowStock();
        detectExpiredContracts();
        detectBrokenMachines();
        detectMaintenanceDue();
        detectFailedLogins();
        detectUnusualStockModifications();
    }

    private void detectLowStock() {
        List<Product> lowStock = productRepository.findLowStockProducts();
        for (Product product : lowStock) {
            createAlertIfNotExists(AlertType.LOW_STOCK, AlertSeverity.HIGH,
                    "Stock faible: " + product.getName(),
                    "Le stock de " + product.getName() + " est à " + product.getCurrentStock()
                            + " (minimum: " + product.getMinimumStock() + ")",
                    "Product", product.getId());
        }
    }

    private void detectExpiredContracts() {
        List<Client> expiring = clientRepository.findByContractExpiryBefore(
                LocalDate.now().plusDays(30));
        for (Client client : expiring) {
            AlertSeverity severity = client.getContractExpiry().isBefore(LocalDate.now())
                    ? AlertSeverity.CRITICAL : AlertSeverity.MEDIUM;
            createAlertIfNotExists(AlertType.EXPIRED_CONTRACT, severity,
                    "Contrat expirant: " + client.getName(),
                    "Le contrat de " + client.getName() + " expire le " + client.getContractExpiry(),
                    "Client", client.getId());
        }
    }

    private void detectBrokenMachines() {
        List<Machine> broken = machineRepository.findByStatus(MachineStatus.BROKEN);
        for (Machine machine : broken) {
            createAlertIfNotExists(AlertType.MACHINE_BROKEN, AlertSeverity.CRITICAL,
                    "Machine en panne: " + machine.getSerialNumber(),
                    "La machine " + machine.getModel() + " (" + machine.getSerialNumber() + ") est en panne",
                    "Machine", machine.getId());
        }
    }

    private void detectMaintenanceDue() {
        List<Machine> due = machineRepository.findByNextMaintenanceBefore(LocalDate.now().plusDays(7));
        for (Machine machine : due) {
            createAlertIfNotExists(AlertType.MAINTENANCE_DUE, AlertSeverity.MEDIUM,
                    "Maintenance requise: " + machine.getSerialNumber(),
                    "Maintenance prévue le " + machine.getNextMaintenance(),
                    "Machine", machine.getId());
        }
    }

    private void detectFailedLogins() {
        long failedCount = loginHistoryRepository.countBySuccessFalseAndCreatedAtAfter(
                LocalDateTime.now().minusHours(1));
        if (failedCount >= 10) {
            createAlertIfNotExists(AlertType.FAILED_LOGIN, AlertSeverity.HIGH,
                    "Tentatives de connexion échouées",
                    failedCount + " tentatives échouées dans la dernière heure",
                    "Security", null);
        }
    }

    private void detectUnusualStockModifications() {
        long lostCount = stockMovementRepository.countByTypeAndCreatedAtAfter(
                StockMovementType.LOST, LocalDateTime.now().minusHours(24));
        long brokenCount = stockMovementRepository.countByTypeAndCreatedAtAfter(
                StockMovementType.BROKEN, LocalDateTime.now().minusHours(24));
        if (lostCount + brokenCount >= 5) {
            createAlertIfNotExists(AlertType.SUSPICIOUS_ACTIVITY, AlertSeverity.HIGH,
                    "Activité suspecte sur le stock",
                    (lostCount + brokenCount) + " mouvements de perte/casse en 24h",
                    "Stock", null);
        }
    }

    private void createAlertIfNotExists(AlertType type, AlertSeverity severity,
                                         String title, String message,
                                         String entityType, Long entityId) {
        boolean exists = alertRepository.findByResolvedFalseOrderByCreatedAtDesc().stream()
                .anyMatch(a -> a.getType() == type && a.getEntityId() != null
                        && a.getEntityId().equals(entityId));
        if (!exists) {
            Alert alert = Alert.builder()
                    .type(type)
                    .severity(severity)
                    .title(title)
                    .message(message)
                    .entityType(entityType)
                    .entityId(entityId)
                    .build();
            alertRepository.save(alert);
        }
    }
}
