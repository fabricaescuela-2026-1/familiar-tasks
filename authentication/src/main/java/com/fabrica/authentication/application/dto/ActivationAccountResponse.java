package com.fabrica.authentication.application.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record ActivationAccountResponse(
  String email,
  UUID userId,
  boolean activated
) {}
