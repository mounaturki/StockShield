package com.ghamem.trading.stockshield.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPeriodDto {
    private Long id;
    private String code;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
