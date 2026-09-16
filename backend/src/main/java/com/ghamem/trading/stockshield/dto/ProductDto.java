package com.ghamem.trading.stockshield.dto;

import com.ghamem.trading.stockshield.entity.ProductCategory;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private ProductCategory category;
    private String categoryLabel;
    private String periodCode;
    private BigDecimal price;
    private Integer currentStock;
    private Integer minimumStock;
    private String supplier;
    private String description;
    private boolean active;
    private boolean lowStock;
}
