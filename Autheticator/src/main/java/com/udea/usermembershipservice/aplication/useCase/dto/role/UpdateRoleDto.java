package com.udea.usermembershipservice.aplication.useCase.dto.role;

import java.util.UUID;

public record UpdateRoleDto(
    UUID idRole,
    String name
) {
}
