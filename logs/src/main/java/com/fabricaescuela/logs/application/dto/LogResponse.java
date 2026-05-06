package com.fabricaescuela.logs.application.dto;

import java.time.LocalDateTime;

public record LogResponse(
        String id,
        String idUser,
        LocalDateTime timestamp,
        String modifiedElement,
        String action
) {}
