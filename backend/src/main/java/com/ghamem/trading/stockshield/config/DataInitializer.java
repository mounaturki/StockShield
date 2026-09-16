package com.ghamem.trading.stockshield.config;

import com.ghamem.trading.stockshield.entity.*;
import com.ghamem.trading.stockshield.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Order(2)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final MachineRepository machineRepository;
    private final ProductRepository productRepository;
    private final ProductPeriodRepository periodRepository;
    private final DiscountRepository discountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        userRepository.save(User.builder()
                .username("admin").email("admin@ghamem.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin").lastName("Ghamem").role(Role.ADMIN).build());

        userRepository.save(User.builder()
                .username("magasinier").email("magasin@ghamem.com")
                .password(passwordEncoder.encode("mag123"))
                .firstName("Ahmed").lastName("Mansouri").role(Role.MAGASINIER)
                .warehouseId(1L).build());

        userRepository.save(User.builder()
                .username("vendeur").email("vendeur@ghamem.com")
                .password(passwordEncoder.encode("ven123"))
                .firstName("Youssef").lastName("Alaoui").role(Role.VENDEUR).build());

        userRepository.save(User.builder()
                .username("secretaire").email("secretariat@ghamem.com")
                .password(passwordEncoder.encode("sec123"))
                .firstName("Nadia").lastName("Berrada").role(Role.SECRETAIRE).build());

        ProductPeriod currentPeriod = periodRepository.save(ProductPeriod.builder()
                .code("2026-S1")
                .name("Saison Été 2026")
                .startDate(LocalDate.of(2026, 4, 1))
                .endDate(LocalDate.of(2026, 9, 30))
                .active(true)
                .build());

        Client client1 = clientRepository.save(Client.builder()
                .name("Café Atlas").address("12 Rue Mohammed V, Casablanca")
                .phone("+212 522 123456").manager("Hassan Alami")
                .contractExpiry(LocalDate.now().plusMonths(6)).build());

        Client client2 = clientRepository.save(Client.builder()
                .name("Restaurant Le Jardin").address("45 Bd Zerktouni, Marrakech")
                .phone("+212 524 789012").manager("Fatima Zahra")
                .contractExpiry(LocalDate.now().plusDays(15)).build());

        machineRepository.save(Machine.builder()
                .serialNumber("GLC-2024-001").model("Eskimo Glacon Pro")
                .machineType(MachineType.GLACON).client(client1)
                .status(MachineStatus.OPERATIONAL)
                .installationDate(LocalDate.of(2024, 3, 15))
                .lastMaintenance(LocalDate.of(2025, 12, 1))
                .nextMaintenance(LocalDate.now().plusDays(5)).build());

        machineRepository.save(Machine.builder()
                .serialNumber("GLC-2024-002").model("Eskimo Glace Compact")
                .machineType(MachineType.GLACE).client(client2)
                .status(MachineStatus.BROKEN)
                .installationDate(LocalDate.of(2024, 6, 20))
                .lastMaintenance(LocalDate.of(2025, 9, 10))
                .nextMaintenance(LocalDate.now().minusDays(10)).build());

        createProduct("Paname STK 5L", ProductCategory.PANAME_STK, currentPeriod.getCode(), 120, 150, 50);
        createProduct("Paname Roché 3kg", ProductCategory.PANAME_ROCHE, currentPeriod.getCode(), 95, 80, 30);
        createProduct("Paname BRKT 2L", ProductCategory.PANAME_BRKT, currentPeriod.getCode(), 85, 60, 25);
        createProduct("Paname CNE 1L", ProductCategory.PANAME_CNE, currentPeriod.getCode(), 75, 45, 20);
        createProduct("Vrac Fraise", ProductCategory.VRAC, currentPeriod.getCode(), 200, 30, 15);
        createProduct("Tropic Mangue", ProductCategory.TROPIC, currentPeriod.getCode(), 110, 55, 20);
        createProduct("Eskimo Classic", ProductCategory.ESKIMO, currentPeriod.getCode(), 130, 70, 25);
        createProduct("Kimo Citron", ProductCategory.KIMO, currentPeriod.getCode(), 90, 40, 15);
        createProduct("Capp Vanille", ProductCategory.CAPP, currentPeriod.getCode(), 100, 35, 15);
        createProduct("Kimcone Chocolat", ProductCategory.KIMCONE, currentPeriod.getCode(), 115, 25, 20);
        createProduct("Glacon Premium", ProductCategory.GLACON, currentPeriod.getCode(), 140, 8, 30);
        createProduct("Magnum Pistache", ProductCategory.MAGNUM, currentPeriod.getCode(), 160, 50, 20);
        createProduct("Flash Menthe", ProductCategory.FLASH, currentPeriod.getCode(), 80, 20, 10);
        createProduct("Zonda Coco", ProductCategory.ZONDA, currentPeriod.getCode(), 105, 5, 15);

        discountRepository.save(Discount.builder()
                .name("Remise fidélité 10%").percentage(new BigDecimal("10.00"))
                .conditions("Client avec plus de 2 ans de contrat").validated(true)
                .validatedBy("admin").build());
    }

    private void createProduct(String name, ProductCategory category, String periodCode,
                               double price, int stock, int minStock) {
        productRepository.save(Product.builder()
                .name(name).category(category).periodCode(periodCode)
                .price(new BigDecimal(String.valueOf(price)))
                .currentStock(stock).minimumStock(minStock)
                .supplier("Ghamem Trading").build());
    }
}
