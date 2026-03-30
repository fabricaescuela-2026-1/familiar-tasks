package com.fabricaescuela.tasks.infraestructure.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fabricaescuela.tasks.domain.exceptions.PriorityNotFoundException;
import com.fabricaescuela.tasks.domain.exceptions.StatusNotFoundException;
import com.fabricaescuela.tasks.infraestructure.presentation.dtos.ProblemDetails;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ProblemDetails> handleNoResourceFoundException(NoResourceFoundException ex) {
    var problemDetails = ProblemDetails.builder()
        .type("https://tasks/resource-not-found")
        .title("Resource Not Found")
        .status(HttpStatus.NOT_FOUND.value())
        .detail(ex.getMessage())
        .instance(ex.getStackTrace()[0].getMethodName())
        .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetails);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();

    var problemDetails = ProblemDetails.builder()
        .type("https://tasks/validation-error")
        .title("Validation Error")
        .status(HttpStatus.BAD_REQUEST.value())
        .detail(String.join(", ", errors))
        .instance(ex.getParameter().getMethod().getName())
        .build();
    return ResponseEntity.badRequest().body(problemDetails);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ProblemDetails> handleIllegalArgumentException(IllegalArgumentException ex) {
    var problemDetails = ProblemDetails.builder()
        .type("https://tasks/illegal-argument")
        .title("Illegal Argument")
        .status(HttpStatus.BAD_REQUEST.value())
        .detail(ex.getMessage())
        .instance(ex.getStackTrace()[0].getMethodName())
        .build();
    return ResponseEntity.badRequest().body(problemDetails);
  }

  @ExceptionHandler(StatusNotFoundException.class)
  public ResponseEntity<ProblemDetails> handleStatusNotFoundException(StatusNotFoundException ex) {
    var problemDetails = ProblemDetails.builder()
        .type("https://tasks/status-not-found")
        .title("Status Not Found")
        .status(HttpStatus.BAD_REQUEST.value())
        .detail(ex.getMessage())
        .instance(ex.getStackTrace()[0].getMethodName())
        .build();
    return ResponseEntity.badRequest().body(problemDetails);
  }

  @ExceptionHandler(PriorityNotFoundException.class)
  public ResponseEntity<ProblemDetails> handlePriorityNotFoundException(PriorityNotFoundException ex) {
    var problemDetails = ProblemDetails.builder()
        .type("https://tasks/priority-not-found")
        .title("Priority Not Found")
        .status(HttpStatus.BAD_REQUEST.value())
        .detail(ex.getMessage())
        .instance(ex.getStackTrace()[0].getMethodName())
        .build();
    return ResponseEntity.badRequest().body(problemDetails);
  }
}
