package com.fabrica.authentication.domain.model;

import com.fabrica.authentication.domain.exceptions.InvalidTowFactorAuthTokenException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TwoFactorAuthToken {

  private UUID id;
  private String codeHash;
  private User user;
  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;
  private boolean invalidated;
  private int attempts;

  public void validate() {
    if (invalidated) {
      throw new InvalidTowFactorAuthTokenException(
        "El codigo es invalidado, asegurate de usar el ultimo codigo enviado a tu email"
      );
    }
    if (LocalDateTime.now().isAfter(expiresAt)) {
      throw new InvalidTowFactorAuthTokenException(
        "El codigo ha expirado, solicita uno nuevo"
      );
    }
    if (attempts >= 3) {
      throw new InvalidTowFactorAuthTokenException(
        "Demasiados intentos fallidos, solicita un codigo nuevo"
      );
    }
  }
}
