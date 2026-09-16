package com.ghamem.trading.stockshield.config;

import com.ghamem.trading.stockshield.entity.*;
import com.ghamem.trading.stockshield.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class LegacyDataMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductPeriodRepository periodRepository;
    private final ClientRepository clientRepository;
    private final MachineRepository machineRepository;
    private final PasswordEncoder passwordEncoder;

    private static final List<String> VALID_CATEGORIES = Arrays.stream(ProductCategory.values())
            .map(Enum::name).collect(Collectors.toList());

    @Override
    @Transactional
    public void run(String... args) {
        dropCheckConstraints("users");
        migrateRoles();
        dropCheckConstraints("products");
        migrateMachineTypes();
        migrateProductsIfNeeded();
        seedMissingUsers();
        log.info("Migration des données terminée");
    }

    private void dropCheckConstraints(String tableName) {
        List<String> constraints = jdbcTemplate.queryForList(
                "SELECT c.conname FROM pg_constraint c " +
                        "JOIN pg_class t ON c.conrelid = t.oid " +
                        "WHERE t.relname = ? AND c.contype = 'c'",
                String.class, tableName);
        for (String constraint : constraints) {
            log.info("Suppression contrainte {} sur {}", constraint, tableName);
            jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP CONSTRAINT IF EXISTS \"" + constraint + "\"");
        }
    }

    private void migrateRoles() {
        int updated = 0;
        updated += jdbcTemplate.update("UPDATE users SET role = 'MAGASINIER' WHERE role = 'WAREHOUSE_KEEPER'");
        updated += jdbcTemplate.update("UPDATE users SET role = 'SECRETAIRE' WHERE role = 'MANAGER'");
        updated += jdbcTemplate.update("UPDATE users SET role = 'VENDEUR' WHERE role = 'SALES'");
        if (updated > 0) {
            log.info("{} utilisateur(s) migré(s) vers les nouveaux rôles", updated);
        }
    }

    private void migrateMachineTypes() {
        jdbcTemplate.update("UPDATE machines SET machine_type = 'GLACE' WHERE machine_type IS NULL");
    }

    private void migrateProductsIfNeeded() {
        Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
        if (productCount == null || productCount == 0) {
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            if (userCount != null && userCount > 0) {
                log.info("Catalogue produits vide — initialisation...");
                ProductPeriod period = ensureCurrentPeriod();
                seedProducts(period.getCode());
            }
            return;
        }

        if (!hasInvalidProductCategories()) {
            ensureCurrentPeriod();
            return;
        }

        log.warn("Anciennes catégories produits détectées — mise à jour du catalogue...");
        jdbcTemplate.update("DELETE FROM inventory_items");
        jdbcTemplate.update("DELETE FROM stock_movements");
        jdbcTemplate.update("DELETE FROM products");

        ProductPeriod period = ensureCurrentPeriod();
        seedProducts(period.getCode());
    }

    private boolean hasInvalidProductCategories() {
        String placeholders = VALID_CATEGORIES.stream().map(c -> "?").collect(Collectors.joining(","));
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products WHERE category IS NULL OR category NOT IN (" + placeholders + ")",
                Integer.class,
                VALID_CATEGORIES.toArray());
        return count != null && count > 0;
    }

    private ProductPeriod ensureCurrentPeriod() {
        return periodRepository.findByActiveTrue().orElseGet(() ->
                periodRepository.save(ProductPeriod.builder()
                        .code("2026-S1")
                        .name("Saison Été 2026")
                        .startDate(LocalDate.of(2026, 4, 1))
                        .endDate(LocalDate.of(2026, 9, 30))
                        .active(true)
                        .build()));
    }

    private void seedProducts(String periodCode) {
        createProduct("Paname STK 5L", ProductCategory.PANAME_STK, periodCode, 120, 150, 50);
        createProduct("Paname Roché 3kg", ProductCategory.PANAME_ROCHE, periodCode, 95, 80, 30);
        createProduct("Paname BRKT 2L", ProductCategory.PANAME_BRKT, periodCode, 85, 60, 25);
        createProduct("Paname CNE 1L", ProductCategory.PANAME_CNE, periodCode, 75, 45, 20);
        createProduct("Vrac Fraise", ProductCategory.VRAC, periodCode, 200, 30, 15);
        createProduct("Tropic Mangue", ProductCategory.TROPIC, periodCode, 110, 55, 20);
        createProduct("Eskimo Classic", ProductCategory.ESKIMO, periodCode, 130, 70, 25);
        createProduct("Kimo Citron", ProductCategory.KIMO, periodCode, 90, 40, 15);
        createProduct("Capp Vanille", ProductCategory.CAPP, periodCode, 100, 35, 15);
        createProduct("Kimcone Chocolat", ProductCategory.KIMCONE, periodCode, 115, 25, 20);
        createProduct("Glacon Premium", ProductCategory.GLACON, periodCode, 140, 8, 30);
        createProduct("Magnum Pistache", ProductCategory.MAGNUM, periodCode, 160, 50, 20);
        createProduct("Flash Menthe", ProductCategory.FLASH, periodCode, 80, 20, 10);
        createProduct("Zonda Coco", ProductCategory.ZONDA, periodCode, 105, 5, 15);
    }

    private void createProduct(String name, ProductCategory category, String periodCode,
                               double price, int stock, int minStock) {
        productRepository.save(Product.builder()
                .name(name).category(category).periodCode(periodCode)
                .price(new BigDecimal(String.valueOf(price)))
                .currentStock(stock).minimumStock(minStock)
                .supplier("Ghamem Trading").build());
    }

    private void seedMissingUsers() {
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        if (userCount == null || userCount == 0) return;
        ensureUser("admin", "admin@ghamem.com", "admin123", "Admin", "Ghamem", Role.ADMIN, null);
        ensureUser("magasinier", "magasin@ghamem.com", "mag123", "Ahmed", "Mansouri", Role.MAGASINIER, 1L);
        ensureUser("vendeur", "vendeur@ghamem.com", "ven123", "Youssef", "Alaoui", Role.VENDEUR, null);
        ensureUser("secretaire", "secretariat@ghamem.com", "sec123", "Nadia", "Berrada", Role.SECRETAIRE, null);
    }

    private void ensureUser(String username, String email, String password,
                            String firstName, String lastName, Role role, Long warehouseId) {
        if (userRepository.findByUsername(username).isEmpty()) {
            userRepository.save(User.builder()
                    .username(username).email(email)
                    .password(passwordEncoder.encode(password))
                    .firstName(firstName).lastName(lastName)
                    .role(role).warehouseId(warehouseId).build());
        }
    }
}
