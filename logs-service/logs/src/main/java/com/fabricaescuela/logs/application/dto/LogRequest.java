package com.fabricaescuela.logs.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record LogRequest(
        @NotBlank(message = "Log ID is required") String id,
        @NotBlank(message = "User ID is required") String idUser,
        @NotBlank(message = "Timestamp is required") LocalDateTime timestamp,
        @NotBlank(message = "Modified element is required") String modifiedElement,
        @NotBlank(message = "Action is required") String action
) {
}
