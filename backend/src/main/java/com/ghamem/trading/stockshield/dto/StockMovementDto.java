package com.ghamem.trading.stockshield.dto;

import com.ghamem.trading.stockshield.entity.StockMovementType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementDto {
    private Long id;
    private Long productId;
    private String productName;
    private StockMovementType type;
    private Integer quantity;
    private String reason;
    private Long warehouseId;
    private String performedBy;
    private LocalDateTime createdAt;
}
