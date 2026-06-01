package com.fabrica.authentication.domain.model;

import com.fabrica.authentication.domain.exceptions.InvalidActivationTokenException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivationToken {

  private UUID id;
  private String email;
  private String codeHash;
  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;
  private int attempts;
  private boolean invalidated;

  public void validateToken() {
    if (attempts >= 3) {
      throw new InvalidActivationTokenException(
        "Has superado el número máximo de intentos, genera un codigo nuevo"
      );
    }
    if (LocalDateTime.now().isAfter(expiresAt)) {
      throw new InvalidActivationTokenException(
        "El token ha expirado, intenta generar uno nuevo"
      );
    }
    if (invalidated) {
      throw new InvalidActivationTokenException(
        "El token ha sido invalidado, asegúrate de ingresar el ultimo codigo enviado a tu email"
      );
    }
  }
}
