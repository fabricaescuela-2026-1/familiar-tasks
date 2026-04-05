package com.fabricaescuela.tasks.domain.exceptions;

import java.util.UUID;

public class UserNotValidException extends RuntimeException {
    private final UUID guestId;
    private final UUID homeId;

    public UserNotValidException(UUID guestId, UUID homeId, String message) {
        super(message);
        this.guestId = guestId;
        this.homeId = homeId;
    }
}
