package com.ghamem.trading.stockshield.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskItemDto {
    private Long id;
    private String title;
    private String description;
    private String severity;
    private String entityType;
    private Long entityId;
}
