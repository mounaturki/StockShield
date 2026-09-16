package com.ghamem.trading.stockshield.controller;

import com.ghamem.trading.stockshield.dto.*;
import com.ghamem.trading.stockshield.entity.*;
import com.ghamem.trading.stockshield.security.AbacPolicyEvaluator;
import com.ghamem.trading.stockshield.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AbacPolicyEvaluator abacPolicyEvaluator;

    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/risk-center")
    public ResponseEntity<RiskCenterDto> getRiskCenter(@AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canViewRiskCenter(user)) {
            throw new SecurityException("Accès refusé — Risk Center réservé à l'administrateur");
        }
        return ResponseEntity.ok(dashboardService.getRiskCenter());
    }

    @GetMapping("/stock-risk")
    public ResponseEntity<StockRiskDto> getStockRisk(@AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canViewStockRisk(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(dashboardService.getStockRisk());
    }
}

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
class AlertController {
    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<AlertDto>> findAll() {
        return ResponseEntity.ok(alertService.findAll());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        alertService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Void> resolve(@PathVariable Long id) {
        alertService.resolve(id);
        return ResponseEntity.noContent().build();
    }
}

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
class AuditController {
    private final AuditLogService auditLogService;
    private final AbacPolicyEvaluator abacPolicyEvaluator;

    @GetMapping
    public ResponseEntity<List<AuditLogDto>> findAll(@AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canViewAuditLogs(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(auditLogService.findAll());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditLogDto>> findRecent(@AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canViewAuditLogs(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(auditLogService.findRecent());
    }
}

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
class DiscountController {
    private final DiscountService discountService;

    @GetMapping
    public ResponseEntity<List<Discount>> findAll() {
        return ResponseEntity.ok(discountService.findAll());
    }

    @PostMapping
    public ResponseEntity<Discount> create(@RequestBody Discount discount, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(discountService.create(discount, user.getUsername()));
    }

    @PatchMapping("/{id}/validate")
    public ResponseEntity<Discount> validate(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(discountService.validate(id, user.getUsername()));
    }
}

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Inventory>> findAll() {
        return ResponseEntity.ok(inventoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<Inventory> create(@RequestBody Map<String, String> body,
                                            @AuthenticationPrincipal User user) {
        InventoryType type = InventoryType.valueOf(body.getOrDefault("type", "MONTHLY"));
        return ResponseEntity.ok(inventoryService.create(type, user.getUsername()));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<InventoryItem>> getItems(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getItems(id));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<InventoryItem> updateItem(@PathVariable Long itemId,
                                                     @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(inventoryService.updateItem(itemId, body.get("actualQuantity")));
    }
}
