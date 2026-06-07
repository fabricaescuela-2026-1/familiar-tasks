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
    if (password.chars().noneMatch(Character::isUpperCase)) {
      throw new IllegalArgumentException(
        "La contraseña debe contener al menos una letra mayúscula"
      );
    }
    if (password.chars().noneMatch(Character::isLowerCase)) {
      throw new IllegalArgumentException(
        "La contraseña debe contener al menos una letra minúscula"
      );
    }
    if (password.chars().noneMatch(Character::isDigit)) {
      throw new IllegalArgumentException(
        "La contraseña debe contener al menos un dígito"
      );
    }
    if (
      password
        .chars()
        .allMatch(c ->
          Character.isUpperCase(c) ||
          Character.isLowerCase(c) ||
          Character.isDigit(c)
        )
    ) {
      throw new IllegalArgumentException(
        "La contraseña debe contener al menos un carácter especial"
      );
    }
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
