package com.udea.usermembershipservice.aplication.useCase.dto.mermberHome;

import java.util.UUID;

public record MemberHomeDto(
    String homeId,
    String personId,
    String name,
    String last_name,
    String homeName,
    String email,
    UUID roleId,
    Boolean active
) {
} 
