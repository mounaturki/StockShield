package com.ghamem.trading.stockshield.dto;

import com.ghamem.trading.stockshield.entity.AlertSeverity;
import com.ghamem.trading.stockshield.entity.AlertType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDto {
    private Long id;
    private AlertType type;
    private AlertSeverity severity;
    private String title;
    private String message;
    private String entityType;
    private Long entityId;
    private boolean read;
    private boolean resolved;
    private LocalDateTime createdAt;
}
