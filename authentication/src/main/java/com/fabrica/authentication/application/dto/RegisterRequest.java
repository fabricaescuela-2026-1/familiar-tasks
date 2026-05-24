package com.fabrica.authentication.application.dto;

public record RegisterRequest(
    String name,
    String lastname,
    String email,
    String password,
    String passwordConfirmation) {

  public RegisterRequest(String name, String lastname, String email, String password) {
    this(name, lastname, email, password, null);
  }
}
