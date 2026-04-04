package com.udea.usermembershipservice.domain.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Password cannot be null or empty and must be at least 8 characters long and contain at least one number.");
    }
}
