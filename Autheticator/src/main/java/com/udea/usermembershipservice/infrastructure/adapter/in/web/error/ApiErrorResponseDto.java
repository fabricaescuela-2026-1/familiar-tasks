package com.udea.usermembershipservice.infrastructure.adapter.in.web.error;

import java.time.LocalDateTime;

public record ApiErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message
) {
}
