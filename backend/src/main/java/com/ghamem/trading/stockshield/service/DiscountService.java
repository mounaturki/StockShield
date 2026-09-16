package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.entity.Discount;
import com.ghamem.trading.stockshield.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final AuditService auditService;

    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    @Transactional
    public Discount create(Discount discount, String performedBy) {
        discount = discountRepository.save(discount);
        auditService.log(null, performedBy, "CREATE_DISCOUNT", "Discount", discount.getId(), null, discount.getName(), null);
        return discount;
    }

    @Transactional
    public Discount validate(Long id, String performedBy) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Remise non trouvée"));
        discount.setValidated(true);
        discount.setValidatedBy(performedBy);
        return discountRepository.save(discount);
    }
}
