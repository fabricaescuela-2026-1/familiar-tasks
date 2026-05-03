package com.udea.usermembershipservice.infrastructure.adapter.in.web.error;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
import com.udea.usermembershipservice.domain.exception.InvalidDataException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ApiErrorResponseDto> handleBadRequest(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SearchException.class)
    public ResponseEntity<ApiErrorResponseDto> handleSearch(SearchException ex) {
        return buildResponse(resolveSearchStatus(ex), resolveMessage(ex));
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ApiErrorResponseDto> handlePersistence(PersistenceException ex) {
        return buildResponse(resolvePersistenceStatus(ex), resolveMessage(ex));
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<ApiErrorResponseDto> handleCompletion(CompletionException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    private ResponseEntity<ApiErrorResponseDto> buildResponse(HttpStatus status, String message) {
        ApiErrorResponseDto errorResponse = new ApiErrorResponseDto(
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
