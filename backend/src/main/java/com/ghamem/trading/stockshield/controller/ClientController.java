package com.ghamem.trading.stockshield.controller;

import com.ghamem.trading.stockshield.dto.ClientDto;
import com.ghamem.trading.stockshield.entity.User;
import com.ghamem.trading.stockshield.security.AbacPolicyEvaluator;
import com.ghamem.trading.stockshield.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final AbacPolicyEvaluator abacPolicyEvaluator;

    @GetMapping
    public ResponseEntity<List<ClientDto>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ClientDto> create(@RequestBody ClientDto dto, @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageClients(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(clientService.create(dto, user.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> update(@PathVariable Long id, @RequestBody ClientDto dto,
                                            @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageClients(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(clientService.update(id, dto, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageClients(user)) {
            throw new SecurityException("Accès refusé");
        }
        clientService.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
