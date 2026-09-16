package com.ghamem.trading.stockshield.controller;

import com.ghamem.trading.stockshield.dto.StockMovementDto;
import com.ghamem.trading.stockshield.dto.StockMovementRequest;
import com.ghamem.trading.stockshield.entity.User;
import com.ghamem.trading.stockshield.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockMovementDto>> findAll() {
        return ResponseEntity.ok(stockService.findAll());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovementDto>> findByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.findByProduct(productId));
    }

    @PostMapping("/movement")
    public ResponseEntity<StockMovementDto> createMovement(@RequestBody StockMovementRequest request,
                                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(stockService.createMovement(request, user));
    }
}
