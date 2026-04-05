package com.fabricaescuela.tasks.domain.ports.out;

import java.util.UUID;

public interface UserValidationPort {
    boolean validateUserInHome(UUID guestId, UUID homeId);
}
