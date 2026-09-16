package com.ghamem.trading.stockshield.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "machines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "machine_type")
    @Builder.Default
    private MachineType machineType = MachineType.GLACE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MachineStatus status = MachineStatus.OPERATIONAL;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "last_maintenance")
    private LocalDate lastMaintenance;

    @Column(name = "next_maintenance")
    private LocalDate nextMaintenance;

    @Column(name = "photo_path")
    private String photoPath;

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
