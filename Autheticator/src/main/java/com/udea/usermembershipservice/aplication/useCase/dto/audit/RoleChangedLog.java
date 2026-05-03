package com.udea.usermembershipservice.aplication.useCase.dto.audit;

import java.util.UUID;

public record RoleChangedLog(
    UUID logId,
    UUID userId,
    String modifiedElement,
    String action
) {
    public static RoleChangedLog roleChanged(UUID userId, String modifiedElement) {
        return new RoleChangedLog(UUID.randomUUID(), userId, modifiedElement, "role_changed");
    }
}
