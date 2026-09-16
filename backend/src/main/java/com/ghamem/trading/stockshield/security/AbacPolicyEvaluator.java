package com.ghamem.trading.stockshield.security;

import com.ghamem.trading.stockshield.entity.Role;
import com.ghamem.trading.stockshield.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class AbacPolicyEvaluator {

    @Value("${app.abac.work-hours-start}")
    private int workHoursStart;

    @Value("${app.abac.work-hours-end}")
    private int workHoursEnd;

    public boolean canModifyStock(User user, Long warehouseId) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (user.getRole() != Role.MAGASINIER) {
            return false;
        }
        if (!isWithinWorkHours()) {
            return false;
        }
        return user.getWarehouseId() == null || user.getWarehouseId().equals(warehouseId);
    }

    public boolean canManageUsers(User user) {
        return user.getRole() == Role.ADMIN;
    }

    public boolean canValidateDiscounts(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.SECRETAIRE;
    }

    public boolean canViewAuditLogs(User user) {
        return user.getRole() == Role.ADMIN;
    }

    public boolean canViewRiskCenter(User user) {
        return user.getRole() == Role.ADMIN;
    }

    public boolean canViewStockRisk(User user) {
        return user.getRole() == Role.ADMIN
                || user.getRole() == Role.MAGASINIER
                || user.getRole() == Role.SECRETAIRE;
    }

    public boolean canManageClients(User user) {
        return user.getRole() == Role.ADMIN
                || user.getRole() == Role.SECRETAIRE
                || user.getRole() == Role.VENDEUR;
    }

    public boolean canManageProducts(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.MAGASINIER;
    }

    public boolean canManageMachines(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.SECRETAIRE;
    }

    private boolean isWithinWorkHours() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(LocalTime.of(workHoursStart, 0))
                && !now.isAfter(LocalTime.of(workHoursEnd, 0));
    }
}
