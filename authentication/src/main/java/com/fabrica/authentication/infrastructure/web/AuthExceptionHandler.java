package com.fabrica.authentication.infrastructure.web;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fabrica.authentication.domain.exceptions.EmailAlreadyExitsException;
import com.fabrica.authentication.domain.exceptions.InvalidRefreshTokenException;
import com.fabrica.authentication.domain.exceptions.InvalidTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;

@RestControllerAdvice
public class AuthExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    return body(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(EmailAlreadyExitsException.class)
  public ResponseEntity<Map<String, Object>> handleEmailExists(EmailAlreadyExitsException ex) {
    return body(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
    return body(HttpStatus.UNAUTHORIZED, "Invalid credentials");
  }

  @ExceptionHandler(InvalidRefreshTokenException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidRefresh(InvalidRefreshTokenException ex) {
    return body(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidToken(InvalidTokenException ex) {
    return body(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  private ResponseEntity<Map<String, Object>> body(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(Map.of(
        "timestamp", LocalDateTime.now().toString(),
        "status", status.value(),
        "error", status.getReasonPhrase(),
        "message", message));
  }
}
