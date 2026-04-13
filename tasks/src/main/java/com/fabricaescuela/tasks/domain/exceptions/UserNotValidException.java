package com.fabricaescuela.tasks.domain.exceptions;

public class UserNotValidException extends RuntimeException {

    public UserNotValidException(String message) {
        super(message);

    }
}
