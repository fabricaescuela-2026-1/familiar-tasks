package com.fabrica.authentication.application.dto;

public record RegisterRequest(
    String name,
    String lastname,
    String email,
    String password) {

}
