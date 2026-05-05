package com.udea.usermembershipservice.aplication.useCase.dto.role;

import java.util.UUID;

public record RoleDto(
    UUID idRole,
    String name
) {
}
