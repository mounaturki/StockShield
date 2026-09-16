package com.ghamem.trading.stockshield.controller;

import com.ghamem.trading.stockshield.dto.MachineDto;
import com.ghamem.trading.stockshield.entity.User;
import com.ghamem.trading.stockshield.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @GetMapping
    public ResponseEntity<List<MachineDto>> findAll() {
        return ResponseEntity.ok(machineService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(machineService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MachineDto> create(@RequestBody MachineDto dto, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(machineService.create(dto, user.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MachineDto> update(@PathVariable Long id, @RequestBody MachineDto dto,
                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(machineService.update(id, dto, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        machineService.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
