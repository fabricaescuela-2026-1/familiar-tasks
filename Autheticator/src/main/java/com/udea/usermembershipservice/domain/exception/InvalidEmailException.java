package com.udea.usermembershipservice.domain.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super("Email cannot be null or empty and must be a valid email address.");
    }

}
