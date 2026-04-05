package com.fabricaescuela.tasks.infraestructure.adapter.out;

import org.springframework.stereotype.Component;

@Component
public class UserValidationPort implements com.fabricaescuela.tasks.domain.ports.out.UserValidationPort {
    @Override
    public boolean validateUserInHome(java.util.UUID guestId, java.util.UUID homeId) {
        // Aquí iría la lógica real para validar el usuario en el hogar,
        // por ejemplo, haciendo una consulta a una base de datos o a un servicio externo.
        // Por ahora, simplemente devolvemos true para simular que el usuario es válido.
        return true;
    }
}
