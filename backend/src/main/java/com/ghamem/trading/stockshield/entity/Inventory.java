package com.ghamem.trading.stockshield.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryType type;

    @Column(name = "inventory_date", nullable = false)
    private LocalDate inventoryDate;

    @Column(name = "performed_by")
    private String performedBy;

    @Builder.Default
    private String status = "IN_PROGRESS";

    @Column(name = "total_discrepancies")
    @Builder.Default
    private Integer totalDiscrepancies = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
