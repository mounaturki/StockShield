package com.ghamem.trading.stockshield.controller;

import com.ghamem.trading.stockshield.dto.*;
import com.ghamem.trading.stockshield.entity.LoginHistory;
import com.ghamem.trading.stockshield.entity.User;
import com.ghamem.trading.stockshield.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.login(request, ip));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody ChangePasswordRequest request) {
        authService.changePassword(user.getUsername(), request);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }

    @GetMapping("/login-history")
    public ResponseEntity<List<LoginHistory>> getLoginHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.getLoginHistory(user.getUsername()));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build());
    }
}
