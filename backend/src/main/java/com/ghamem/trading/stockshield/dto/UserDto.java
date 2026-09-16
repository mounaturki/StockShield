package com.ghamem.trading.stockshield.dto;

import com.ghamem.trading.stockshield.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private Long warehouseId;
    private boolean enabled;
    private String lastLogin;
}
