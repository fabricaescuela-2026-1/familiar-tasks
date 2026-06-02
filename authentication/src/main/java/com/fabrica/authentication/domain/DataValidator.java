package com.fabrica.authentication.domain;

import com.fabrica.authentication.application.dto.RegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class DataValidator {

  private void validatePasswordComplexity(String password) {
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password is required");
    }
    if (password.length() < 8) {
      throw new IllegalArgumentException(
        "Password must be at least 8 characters"
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
      "Password must contain at least one uppercase letter"
    );
    if (!hasLower) throw new IllegalArgumentException(
      "Password must contain at least one lowercase letter"
    );
    if (!hasDigit) throw new IllegalArgumentException(
      "Password must contain at least one digit"
    );
    if (!hasSpecial) throw new IllegalArgumentException(
      "Password must contain at least one special character"
    );
  }

  public void validateNewUserRequest(RegisterRequest req) {
    if (req.name() == null || req.name().isBlank()) {
      throw new IllegalArgumentException("Name is required");
    }
    if (req.lastname() == null || req.lastname().isBlank()) {
      throw new IllegalArgumentException("Lastname is required");
    }
    if (req.email() == null || req.email().isBlank()) {
      throw new IllegalArgumentException("Email is required");
    }
    validatePasswordComplexity(req.password());
    if (
      req.passwordConfirmation() != null &&
      !req.password().equals(req.passwordConfirmation())
    ) {
      throw new IllegalArgumentException(
        "Password and confirmation do not match"
      );
    }
  }
}
