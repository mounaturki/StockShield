package com.ghamem.trading.stockshield.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {
    private long totalClients;
    private long totalMachines;
    private long totalProducts;
    private long activeAlerts;
    private long failedLogins;
    private long lowStockProducts;
    private long inventoryDiscrepancies;
    private List<AlertDto> recentAlerts;
}
