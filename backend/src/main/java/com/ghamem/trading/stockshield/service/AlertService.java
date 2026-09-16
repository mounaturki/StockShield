package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.AlertDto;
import com.ghamem.trading.stockshield.entity.Alert;
import com.ghamem.trading.stockshield.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    public List<AlertDto> findAll() {
        return alertRepository.findByResolvedFalseOrderByCreatedAtDesc()
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public void markAsRead(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
        alert.setRead(true);
        alertRepository.save(alert);
    }

    @Transactional
    public void resolve(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
        alert.setResolved(true);
        alertRepository.save(alert);
    }

    private AlertDto toDto(Alert alert) {
        return AlertDto.builder()
                .id(alert.getId())
                .type(alert.getType())
                .severity(alert.getSeverity())
                .title(alert.getTitle())
                .message(alert.getMessage())
                .entityType(alert.getEntityType())
                .entityId(alert.getEntityId())
                .read(alert.isRead())
                .resolved(alert.isResolved())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
