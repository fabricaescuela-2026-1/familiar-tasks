package com.fabrica.authentication.infrastructure.web;

import com.fabrica.authentication.application.dto.ActivationAccountRequest;
import com.fabrica.authentication.application.dto.ActivationAccountResponse;
import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;
import com.fabrica.authentication.application.ports.in.AccountValidationUseCase;
import com.fabrica.authentication.application.ports.in.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
  name = "Authentication management",
  description = "authentication management with jwt tokens"
)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthUseCase authUseCase;
  private final AccountValidationUseCase accountValidationUseCase;

  @Operation(
    summary = "login a user",
    description = "get an acces token and a refresh token using username - password authentication",
    method = "POST"
  )
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    return new ResponseEntity<>(authUseCase.login(request), HttpStatus.OK);
  }

  @Operation(
    summary = "register a new user on syste",
    description = "register a new user on system and get a acces token and a refresh token",
    method = "POST"
  )
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(
    @RequestBody RegisterRequest request
  ) {
    return new ResponseEntity<>(
      authUseCase.register(request),
      HttpStatus.CREATED
    );
  }

  @Operation(
    summary = "refresh a jwt token",
    description = "get a new acces token using a valid refresh token",
    method = "POST"
  )
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(
    @RequestBody String refreshToken
  ) {
    return new ResponseEntity<>(
      authUseCase.refreshToken(refreshToken),
      HttpStatus.OK
    );
  }

  @Operation(
    summary = "Get token  details",
    description = "Get token details seding the token hash",
    method = "GET"
  )
  @GetMapping("/token/{tokenHash}")
  public ResponseEntity<TokenResponse> getToken(
    @PathVariable("tokenHash") String tokenHash
  ) {
    return ResponseEntity.ok(authUseCase.getToken(tokenHash));
  }

  @PostMapping("/activate/code")
  public ResponseEntity<Void> getActivationCode(
    @RequestParam("email") String email
  ) {
    accountValidationUseCase.createActivationToken(email);
    return ResponseEntity.ok().build();
  }

  @Operation(
    summary = "activate a user account",
    description = "activate a user account using the activation token",
    method = "GET"
  )
  @PostMapping("/activate")
  public ResponseEntity<ActivationAccountResponse> activateAccount(
    @RequestBody ActivationAccountRequest request
  ) {
    return ResponseEntity.ok(
      accountValidationUseCase.activateAccount(request.email(), request.code())
    );
  }
}
