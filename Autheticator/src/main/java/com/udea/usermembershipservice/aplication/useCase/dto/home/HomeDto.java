package com.udea.usermembershipservice.aplication.useCase.dto.home;

import java.time.LocalDateTime;
import java.util.UUID;

public record HomeDto(
    UUID idHome,
    String name,
    LocalDateTime createdAt
) {
}
