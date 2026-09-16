package com.ghamem.trading.stockshield.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String manager;
    private String contractPath;
    private LocalDate contractExpiry;
    private boolean active;
}
