package com.ghamem.trading.stockshield.controller;

import com.ghamem.trading.stockshield.dto.CreateUserRequest;
import com.ghamem.trading.stockshield.dto.UserDto;
import com.ghamem.trading.stockshield.entity.User;
import com.ghamem.trading.stockshield.security.AbacPolicyEvaluator;
import com.ghamem.trading.stockshield.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AbacPolicyEvaluator abacPolicyEvaluator;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll(@AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageUsers(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageUsers(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserRequest request, @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageUsers(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(userService.create(request, user.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody CreateUserRequest request,
                                          @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageUsers(user)) {
            throw new SecurityException("Accès refusé");
        }
        return ResponseEntity.ok(userService.update(id, request, user.getUsername()));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (!abacPolicyEvaluator.canManageUsers(user)) {
            throw new SecurityException("Accès refusé");
        }
        userService.disable(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
