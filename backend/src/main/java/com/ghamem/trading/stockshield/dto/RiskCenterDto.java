package com.ghamem.trading.stockshield.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskCenterDto {
    private List<RiskItemDto> riskyUsers;
    private List<RiskItemDto> criticalProducts;
    private List<RiskItemDto> maintenanceMachines;
    private List<RiskItemDto> expiringContracts;
    private List<RiskItemDto> inventoryAnomalies;
}
