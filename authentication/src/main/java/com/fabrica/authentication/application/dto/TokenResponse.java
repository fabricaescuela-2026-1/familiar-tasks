package com.fabrica.authentication.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record TokenResponse(
    UUID tokenId,
    String tokenHash,
    LocalDateTime expirationDate,
    UUID userId,
    String tokenType,
    LocalDateTime expiratedAt) {
}