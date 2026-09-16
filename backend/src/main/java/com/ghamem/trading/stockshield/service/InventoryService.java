package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.entity.*;
import com.ghamem.trading.stockshield.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final ProductRepository productRepository;
    private final AuditService auditService;

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Transactional
    public Inventory create(InventoryType type, String performedBy) {
        Inventory inventory = Inventory.builder()
                .type(type)
                .inventoryDate(LocalDate.now())
                .performedBy(performedBy)
                .build();
        inventory = inventoryRepository.save(inventory);

        List<Product> products = productRepository.findByActiveTrue();
        for (Product product : products) {
            InventoryItem item = InventoryItem.builder()
                    .inventory(inventory)
                    .product(product)
                    .expectedQuantity(product.getCurrentStock())
                    .actualQuantity(product.getCurrentStock())
                    .discrepancy(0)
                    .build();
            inventoryItemRepository.save(item);
        }
        inventory.setTotalDiscrepancies(0);
        inventory.setStatus("COMPLETED");
        inventoryRepository.save(inventory);
        auditService.log(null, performedBy, "CREATE_INVENTORY", "Inventory", inventory.getId(), null, type.name(), null);
        return inventory;
    }

    @Transactional
    public InventoryItem updateItem(Long itemId, Integer actualQuantity) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item non trouvé"));
        item.setActualQuantity(actualQuantity);
        item.setDiscrepancy(actualQuantity - item.getExpectedQuantity());
        return inventoryItemRepository.save(item);
    }

    public List<InventoryItem> getItems(Long inventoryId) {
        return inventoryItemRepository.findByInventoryId(inventoryId);
    }
}
