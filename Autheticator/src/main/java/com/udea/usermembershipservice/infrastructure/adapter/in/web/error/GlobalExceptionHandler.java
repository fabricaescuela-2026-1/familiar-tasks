package com.udea.usermembershipservice.infrastructure.adapter.in.web.error;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.udea.usermembershipservice.aplication.useCase.exception.LoginException;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
import com.udea.usermembershipservice.domain.exception.InvalidDataException;
import com.udea.usermembershipservice.domain.exception.InvalidEmailException;
import com.udea.usermembershipservice.domain.exception.InvalidPasswordException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        InvalidDataException.class,
        InvalidEmailException.class,
        InvalidPasswordException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(LoginException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(SearchException.class)
    public ResponseEntity<ApiErrorResponse> handleSearch(SearchException ex) {
        return buildResponse(resolveSearchStatus(ex), resolveMessage(ex));
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ApiErrorResponse> handlePersistence(PersistenceException ex) {
        return buildResponse(resolvePersistenceStatus(ex), resolveMessage(ex));
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<ApiErrorResponse> handleCompletion(CompletionException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    private HttpStatus resolveSearchStatus(SearchException ex) {
        if (hasCause(ex, "not found")) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private HttpStatus resolvePersistenceStatus(PersistenceException ex) {
        if (hasCause(ex, "already exists")) {
            return HttpStatus.CONFLICT;
        }
        if (hasCause(ex, "invalid")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveMessage(RuntimeException ex) {
        Throwable cause = ex.getCause();
        if (cause != null && cause.getMessage() != null && !cause.getMessage().isBlank()) {
            return cause.getMessage();
        }
        return ex.getMessage();
    }

    private boolean hasCause(RuntimeException ex, String text) {
        Throwable cause = ex.getCause();
        if (cause == null || cause.getMessage() == null) {
            return false;
        }
        return cause.getMessage().toLowerCase().contains(text.toLowerCase());
    }
}
