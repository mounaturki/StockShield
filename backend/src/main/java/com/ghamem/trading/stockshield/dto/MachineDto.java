package com.ghamem.trading.stockshield.dto;

import com.ghamem.trading.stockshield.entity.MachineStatus;
import com.ghamem.trading.stockshield.entity.MachineType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MachineDto {
    private Long id;
    private String serialNumber;
    private String model;
    private MachineType machineType;
    private String machineTypeLabel;
    private Long clientId;
    private String clientName;
    private MachineStatus status;
    private LocalDate installationDate;
    private LocalDate lastMaintenance;
    private LocalDate nextMaintenance;
    private String photoPath;
    private String notes;
}
