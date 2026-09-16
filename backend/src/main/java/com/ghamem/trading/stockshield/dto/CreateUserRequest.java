package com.ghamem.trading.stockshield.dto;

import com.ghamem.trading.stockshield.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private Long warehouseId;
}
