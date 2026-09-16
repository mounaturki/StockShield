package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.ProductDto;
import com.ghamem.trading.stockshield.entity.Product;
import com.ghamem.trading.stockshield.entity.ProductPeriod;
import com.ghamem.trading.stockshield.repository.ProductPeriodRepository;
import com.ghamem.trading.stockshield.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPeriodRepository periodRepository;
    private final AuditService auditService;

    public List<ProductDto> findAll() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<ProductDto> findCurrentPeriod() {
        String periodCode = periodRepository.findByActiveTrue()
                .map(ProductPeriod::getCode)
                .orElse(null);
        if (periodCode == null) {
            return findAll();
        }
        return productRepository.findByPeriodCodeAndActiveTrue(periodCode)
                .stream().map(this::toDto).toList();
    }

    public ProductDto findById(Long id) {
        return toDto(productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé")));
    }

    @Transactional
    public ProductDto create(ProductDto dto, String performedBy) {
        String periodCode = dto.getPeriodCode();
        if (periodCode == null) {
            periodCode = periodRepository.findByActiveTrue()
                    .map(ProductPeriod::getCode).orElse("DEFAULT");
        }
        Product product = Product.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .periodCode(periodCode)
                .price(dto.getPrice())
                .currentStock(dto.getCurrentStock() != null ? dto.getCurrentStock() : 0)
                .minimumStock(dto.getMinimumStock())
                .supplier(dto.getSupplier())
                .description(dto.getDescription())
                .build();
        product = productRepository.save(product);
        auditService.log(null, performedBy, "CREATE_PRODUCT", "Product", product.getId(), null, product.getName(), null);
        return toDto(product);
    }

    @Transactional
    public ProductDto update(Long id, ProductDto dto, String performedBy) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        if (dto.getPeriodCode() != null) {
            product.setPeriodCode(dto.getPeriodCode());
        }
        product.setPrice(dto.getPrice());
        product.setMinimumStock(dto.getMinimumStock());
        product.setSupplier(dto.getSupplier());
        product.setDescription(dto.getDescription());
        product = productRepository.save(product);
        auditService.log(null, performedBy, "UPDATE_PRODUCT", "Product", product.getId(), null, product.getName(), null);
        return toDto(product);
    }

    @Transactional
    public void delete(Long id, String performedBy) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        product.setActive(false);
        productRepository.save(product);
        auditService.log(null, performedBy, "DELETE_PRODUCT", "Product", id, product.getName(), null, null);
    }

    private ProductDto toDto(Product product) {
        boolean lowStock = product.getMinimumStock() != null
                && product.getCurrentStock() <= product.getMinimumStock();
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .categoryLabel(product.getCategory() != null ? product.getCategory().getLabel() : null)
                .periodCode(product.getPeriodCode())
                .price(product.getPrice())
                .currentStock(product.getCurrentStock())
                .minimumStock(product.getMinimumStock())
                .supplier(product.getSupplier())
                .description(product.getDescription())
                .active(product.isActive())
                .lowStock(lowStock)
                .build();
    }
}
