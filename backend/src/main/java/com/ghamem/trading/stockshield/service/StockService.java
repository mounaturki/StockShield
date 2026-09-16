package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.StockMovementDto;
import com.ghamem.trading.stockshield.dto.StockMovementRequest;
import com.ghamem.trading.stockshield.entity.Product;
import com.ghamem.trading.stockshield.entity.StockMovement;
import com.ghamem.trading.stockshield.entity.StockMovementType;
import com.ghamem.trading.stockshield.entity.User;
import com.ghamem.trading.stockshield.repository.ProductRepository;
import com.ghamem.trading.stockshield.repository.StockMovementRepository;
import com.ghamem.trading.stockshield.security.AbacPolicyEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final AbacPolicyEvaluator abacPolicyEvaluator;
    private final AuditService auditService;

    public List<StockMovementDto> findAll() {
        return stockMovementRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<StockMovementDto> findByProduct(Long productId) {
        return stockMovementRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public StockMovementDto createMovement(StockMovementRequest request, User user) {
        if (!abacPolicyEvaluator.canModifyStock(user, request.getWarehouseId())) {
            throw new SecurityException("Accès refusé: politique ABAC - modification de stock non autorisée");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        int newStock = calculateNewStock(product.getCurrentStock(), request.getType(), request.getQuantity());
        if (newStock < 0) {
            throw new RuntimeException("Stock insuffisant");
        }

        product.setCurrentStock(newStock);
        productRepository.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .type(request.getType())
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .warehouseId(request.getWarehouseId())
                .performedBy(user.getUsername())
                .build();
        movement = stockMovementRepository.save(movement);

        auditService.log(user.getId(), user.getUsername(), "STOCK_" + request.getType().name(),
                "Product", product.getId(),
                String.valueOf(product.getCurrentStock() - newStock + request.getQuantity()),
                String.valueOf(newStock), null);

        return toDto(movement);
    }

    private int calculateNewStock(int current, StockMovementType type, int quantity) {
        return switch (type) {
            case ENTRY, RETURN -> current + quantity;
            case EXIT, LOST, BROKEN -> current - quantity;
        };
    }

    private StockMovementDto toDto(StockMovement movement) {
        return StockMovementDto.builder()
                .id(movement.getId())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .type(movement.getType())
                .quantity(movement.getQuantity())
                .reason(movement.getReason())
                .warehouseId(movement.getWarehouseId())
                .performedBy(movement.getPerformedBy())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}
