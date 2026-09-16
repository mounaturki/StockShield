package com.ghamem.trading.stockshield.service;

import com.ghamem.trading.stockshield.dto.CreateUserRequest;
import com.ghamem.trading.stockshield.dto.UserDto;
import com.ghamem.trading.stockshield.entity.User;
import com.ghamem.trading.stockshield.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public UserDto findById(Long id) {
        return toDto(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé")));
    }

    @Transactional
    public UserDto create(CreateUserRequest request, String performedBy) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .warehouseId(request.getWarehouseId())
                .build();
        user = userRepository.save(user);
        auditService.log(null, performedBy, "CREATE_USER", "User", user.getId(), null, user.getUsername(), null);
        return toDto(user);
    }

    @Transactional
    public UserDto update(Long id, CreateUserRequest request, String performedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());
        user.setWarehouseId(request.getWarehouseId());
        user = userRepository.save(user);
        auditService.log(null, performedBy, "UPDATE_USER", "User", user.getId(), null, user.getUsername(), null);
        return toDto(user);
    }

    @Transactional
    public void disable(Long id, String performedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setEnabled(false);
        userRepository.save(user);
        auditService.log(null, performedBy, "DISABLE_USER", "User", user.getId(), null, null, null);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .warehouseId(user.getWarehouseId())
                .enabled(user.isEnabled())
                .lastLogin(user.getLastLogin() != null ? user.getLastLogin().toString() : null)
                .build();
    }
}
