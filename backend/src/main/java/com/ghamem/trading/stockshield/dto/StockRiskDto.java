package com.ghamem.trading.stockshield.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockRiskDto {
    private List<RiskItemDto> criticalProducts;
    private List<RiskItemDto> inventoryAnomalies;
    private List<RiskItemDto> stockAlerts;
    private long lowStockCount;
    private long discrepancyCount;
}
