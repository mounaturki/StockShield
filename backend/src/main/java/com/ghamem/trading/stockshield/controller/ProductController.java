package com.ghamem.trading.stockshield.controller;

import com.ghamem.trading.stockshield.dto.ProductDto;
import com.ghamem.trading.stockshield.entity.User;
import com.ghamem.trading.stockshield.security.AbacPolicyEvaluator;
import com.ghamem.trading.stockshield.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AbacPolicyEvaluator abacPolicyEvaluator;

    @GetMapping
    public ResponseEntity<List<ProductDto>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/current-period")
    public ResponseEntity<List<ProductDto>> findCurrentPeriod() {
        return ResponseEntity.ok(productService.findCurrentPeriod());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ProductDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto, @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageProducts(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(productService.create(dto, user.getUsername()));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody ProductDto dto,
                                             @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageProducts(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(productService.update(id, dto, user.getUsername()));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageProducts(user)) {
            throw new SecurityException("Accès refusé");
        }
        productService.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
