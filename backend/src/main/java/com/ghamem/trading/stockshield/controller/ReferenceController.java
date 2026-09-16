package com.ghamem.trading.stockshield.controller;

import com.ghamem.trading.stockshield.dto.ProductPeriodDto;
import com.ghamem.trading.stockshield.service.ReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceService referenceService;

    @GetMapping("/product-categories")
    public ResponseEntity<List<Map<String, String>>> getProductCategories() {
        return ResponseEntity.ok(referenceService.getProductCategories());
    }

    @GetMapping("/periods")
    public ResponseEntity<List<ProductPeriodDto>> getPeriods() {
        return ResponseEntity.ok(referenceService.getPeriods());
    }

    @GetMapping("/current-period")
    public ResponseEntity<ProductPeriodDto> getCurrentPeriod() {
        ProductPeriodDto period = referenceService.getCurrentPeriod();
        return period != null ? ResponseEntity.ok(period) : ResponseEntity.noContent().build();
    }
}
