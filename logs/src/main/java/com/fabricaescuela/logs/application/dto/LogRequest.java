package com.fabricaescuela.logs.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LogRequest(
        @NotBlank(message = "Log ID is required") String id,
        @NotBlank(message = "User ID is required") String idUser,
        @NotBlank(message = "Modified element is required") String modifiedElement,
        @NotBlank(message = "Action is required") String action
) {
}
