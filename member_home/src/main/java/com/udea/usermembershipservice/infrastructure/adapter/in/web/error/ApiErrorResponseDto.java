package com.udea.usermembershipservice.infrastructure.adapter.in.web.error;

import java.time.LocalDateTime;

public record ApiErrorResponseDto(
    LocalDateTime timestamp,
    int status,
    String error,
    String message
) {
}
