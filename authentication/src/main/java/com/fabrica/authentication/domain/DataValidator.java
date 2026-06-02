package com.fabrica.authentication.domain;

import com.fabrica.authentication.application.dto.RegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class DataValidator {

  private void validatePasswordComplexity(String password) {
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("La contraseña es requerida");
    }
    if (password.length() < 8) {
      throw new IllegalArgumentException(
        "La contraseña debe tener al menos 8 caracteres"
      );
    }
    boolean hasUpper = false;
    boolean hasLower = false;
    boolean hasDigit = false;
    boolean hasSpecial = false;
    for (int i = 0; i < password.length(); i++) {
      char c = password.charAt(i);
      if (Character.isUpperCase(c)) hasUpper = true;
      else if (Character.isLowerCase(c)) hasLower = true;
      else if (Character.isDigit(c)) hasDigit = true;
      else hasSpecial = true;
    }
    if (!hasUpper) throw new IllegalArgumentException(
      "La contraseña debe contener al menos una letra mayúscula"
    );
    if (!hasLower) throw new IllegalArgumentException(
      "La contraseña debe contener al menos una letra minúscula"
    );
    if (!hasDigit) throw new IllegalArgumentException(
      "La contraseña debe contener al menos un dígito"
    );
    if (!hasSpecial) throw new IllegalArgumentException(
      "La contraseña debe contener al menos un carácter especial"
    );
  }

  public void validateNewUserRequest(RegisterRequest req) {
    if (req.name() == null || req.name().isBlank()) {
      throw new IllegalArgumentException("El nombre es requerido");
    }
    if (req.lastname() == null || req.lastname().isBlank()) {
      throw new IllegalArgumentException("El apellido es requerido");
    }
    if (req.email() == null || req.email().isBlank()) {
      throw new IllegalArgumentException("El email es requerido");
    }
    validatePasswordComplexity(req.password());
    if (
      req.passwordConfirmation() != null &&
      !req.password().equals(req.passwordConfirmation())
    ) {
      throw new IllegalArgumentException(
        "La contraseña y la confirmación no coinciden"
      );
    }
  }
}
