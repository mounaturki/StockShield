package com.ghamem.trading.stockshield.dto;

import com.ghamem.trading.stockshield.entity.StockMovementType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementRequest {
    private Long productId;
    private StockMovementType type;
    private Integer quantity;
    private String reason;
    private Long warehouseId;
}
