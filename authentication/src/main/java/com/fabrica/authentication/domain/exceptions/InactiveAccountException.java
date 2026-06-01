package com.fabrica.authentication.domain.exceptions;

public class InactiveAccountException extends RuntimeException {

  public InactiveAccountException() {
    super(
      "Aun no has activado tu cuenta, activa tu cuenta antes de ingresar a la aplicación"
    );
  }
}
