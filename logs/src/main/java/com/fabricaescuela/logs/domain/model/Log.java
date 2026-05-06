package com.fabricaescuela.logs.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record Log(
        String id,
        String idUser,
        LocalDateTime timestamp,
        String modifiedElement,
        String action
) {
    public Log {
        Objects.requireNonNull(idUser, "User ID is required");
        Objects.requireNonNull(modifiedElement, "Element is required");
        Objects.requireNonNull(action, "Action is required");

        if (idUser.isBlank()) throw new IllegalArgumentException("User ID cannot be blank");
    }
}
