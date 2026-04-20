package com.fabrica.authentication.infrastructure.web;

import org.springframework.web.bind.annotation.RestController;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.ports.in.AuthUseCase;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthUseCase authUseCase;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    return new ResponseEntity<>(authUseCase.login(request), HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
    return new ResponseEntity<>(authUseCase.register(request), HttpStatus.CREATED);
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@RequestBody String refreshToken) {
    return new ResponseEntity<>(authUseCase.refreshToken(refreshToken), HttpStatus.OK);
  }
}
