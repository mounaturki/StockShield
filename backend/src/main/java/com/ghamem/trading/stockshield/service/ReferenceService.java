package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.ProductPeriodDto;
import com.ghamem.trading.stockshield.entity.ProductCategory;
import com.ghamem.trading.stockshield.entity.ProductPeriod;
import com.ghamem.trading.stockshield.repository.ProductPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final ProductPeriodRepository periodRepository;

    public List<Map<String, String>> getProductCategories() {
        return Arrays.stream(ProductCategory.values())
                .map(c -> Map.of("value", c.name(), "label", c.getLabel()))
                .collect(Collectors.toList());
    }

    public List<ProductPeriodDto> getPeriods() {
        return periodRepository.findAll().stream().map(this::toDto).toList();
    }

    public ProductPeriodDto getCurrentPeriod() {
        return periodRepository.findByActiveTrue()
                .map(this::toDto)
                .orElse(null);
    }

    private ProductPeriodDto toDto(ProductPeriod period) {
        return ProductPeriodDto.builder()
                .id(period.getId())
                .code(period.getCode())
                .name(period.getName())
                .startDate(period.getStartDate())
                .endDate(period.getEndDate())
                .active(period.isActive())
                .build();
    }
}
