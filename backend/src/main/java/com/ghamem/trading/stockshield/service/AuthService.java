package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.*;
import com.ghamem.trading.stockshield.entity.*;
import com.ghamem.trading.stockshield.repository.*;
import com.ghamem.trading.stockshield.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditService auditService;

    @Value("${app.security.max-login-attempts}")
    private int maxLoginAttempts;

    @Value("${app.security.lock-duration-minutes}")
    private int lockDurationMinutes;

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        if (!user.isEnabled()) {
            throw new BadCredentialsException("Compte désactivé");
        }

        if (user.isAccountLocked()) {
            if (user.getLockTime() != null && user.getLockTime().plusMinutes(lockDurationMinutes).isAfter(LocalDateTime.now())) {
                logLogin(request.getUsername(), ipAddress, false, "Compte verrouillé");
                throw new LockedException("Compte verrouillé. Réessayez plus tard.");
            }
            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
            user.setLockTime(null);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user, ipAddress);
            throw new BadCredentialsException("Identifiants invalides");
        }

        user.setFailedLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        logLogin(request.getUsername(), ipAddress, true, null);

        String token = jwtTokenProvider.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Mot de passe actuel incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        auditService.log(user.getId(), username, "CHANGE_PASSWORD", "User", user.getId(), null, null, null);
    }

    public List<LoginHistory> getLoginHistory(String username) {
        return loginHistoryRepository.findByUsernameOrderByCreatedAtDesc(username);
    }

    private void handleFailedLogin(User user, String ipAddress) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= maxLoginAttempts) {
            user.setAccountLocked(true);
            user.setLockTime(LocalDateTime.now());
        }
        userRepository.save(user);
        logLogin(user.getUsername(), ipAddress, false, "Mot de passe incorrect");
    }

    private void logLogin(String username, String ipAddress, boolean success, String reason) {
        LoginHistory history = LoginHistory.builder()
                .username(username)
                .ipAddress(ipAddress)
                .success(success)
                .failureReason(reason)
                .build();
        loginHistoryRepository.save(history);
    }
}
