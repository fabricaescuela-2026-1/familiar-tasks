package com.fabrica.authentication.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserMessage(
    UUID userId,
    String name,
    String lastname,
    String passwordHash,
    String email,
    LocalDateTime createdAt) {

}
