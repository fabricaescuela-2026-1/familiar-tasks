package com.fabrica.authentication.infrastructure.web;

import com.fabrica.authentication.application.dto.ActivationAccountResponse;
import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.CodeAuthRequest;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;
import com.fabrica.authentication.application.ports.in.AccountValidationUseCase;
import com.fabrica.authentication.application.ports.in.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
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

  private static final String MESSAGE_KEY = "message";

  private final AuthUseCase authUseCase;
  private final AccountValidationUseCase accountValidationUseCase;

  @Operation(
    summary = "login a user",
    description = "create a two factor authentication code and send it to the user's email",
    method = "POST"
  )
  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(
    @RequestBody LoginRequest request
  ) {
    authUseCase.login(request);
    return new ResponseEntity<>(
      Map.of(MESSAGE_KEY, "Revisa tu correo e ingresa el código de verificación"),
      HttpStatus.OK
    );
  }

  @Operation(
    summary = "register a new user on syste",
    description = "register a new user on system",
    method = "POST"
  )
  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(
    @RequestBody RegisterRequest request
  ) {
    authUseCase.register(request);
    return new ResponseEntity<>(
      Map.of(MESSAGE_KEY, "Usuario registrado exitosamente"),
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
    description = "Get token details sending the token hash",
    method = "GET"
  )
  @GetMapping("/token/{tokenHash}")
  public ResponseEntity<TokenResponse> getToken(
    @PathVariable("tokenHash") String tokenHash
  ) {
    return ResponseEntity.ok(authUseCase.getToken(tokenHash));
  }

  @PostMapping("/activate/code")
  public ResponseEntity<Map<String, String>> getActivationCode(
    @RequestParam("email") String email
  ) {
    accountValidationUseCase.createActivationToken(email);
    return ResponseEntity.ok().body(
      Map.of(MESSAGE_KEY, "Código de activación enviado")
    );
  }

  @PostMapping("/login/verification-code")
  public ResponseEntity<AuthResponse> verifyTwoFactorAuthCode(
    @RequestBody CodeAuthRequest request
  ) {
    return ResponseEntity.ok(
      authUseCase.verifyTwoFactorAuthCode(request.code(), request.email())
    );
  }

  @Operation(
    summary = "activate a user account",
    description = "activate a user account using the activation token",
    method = "GET"
  )
  @PostMapping("/activate")
  public ResponseEntity<ActivationAccountResponse> activateAccount(
    @RequestBody CodeAuthRequest request
  ) {
    return ResponseEntity.ok(
      accountValidationUseCase.activateAccount(request.email(), request.code())
    );
  }
}
