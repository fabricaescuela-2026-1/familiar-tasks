package com.fabrica.authentication.infrastructure;

import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import com.fabrica.authentication.infrastructure.database.entities.mappers.UserEntityMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityMapperTest {

    private final UserEntityMapper mapper = new UserEntityMapper();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toEntityConvierteDominioAEntidad() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        User user = User.builder()
            .userId(id)
            .name("Carlos")
            .lastname("Ruiz")
            .email("carlos@mail.com")
            .passwordHash("hash-pass")
            .isActive(true)
            .createdAt(ahora)
            .build();

        // Act
        UserEntity entity = mapper.toEntity(user);

        // Assert
        assertEquals(id,              entity.getUserId());
        assertEquals("Carlos",        entity.getName());
        assertEquals("Ruiz",          entity.getLastname());
        assertEquals("carlos@mail.com", entity.getEmail());
        assertEquals("hash-pass",     entity.getPasswordHash());
        assertTrue(entity.getIsActive());
    }

    @Test
    void toDomainConvierteEntidadADominio() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        UserEntity entity = UserEntity.builder()
            .userId(id)
            .name("Carlos")
            .lastname("Ruiz")
            .email("carlos@mail.com")
            .passwordHash("hash-pass")
            .isActive(true)
            .createdAt(ahora)
            .build();

        // Act
        User user = mapper.toDomain(entity);

        // Assert
        assertEquals(id,              user.getUserId());
        assertEquals("Carlos",        user.getName());
        assertEquals("Ruiz",          user.getLastname());
        assertEquals("carlos@mail.com", user.getEmail());
        assertEquals("hash-pass",     user.getPasswordHash());
        assertTrue(user.isActive());
    }

    @Test
    void conversionIdaYVueltaEsConsistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        User original = User.builder()
            .userId(id)
            .name("Carlos")
            .lastname("Ruiz")
            .email("carlos@mail.com")
            .passwordHash("hash-pass")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

        // Act
        User resultado = mapper.toDomain(mapper.toEntity(original));

        // Assert
        assertEquals(original.getUserId(), resultado.getUserId());
        assertEquals(original.getEmail(),  resultado.getEmail());
        assertEquals(original.getName(),   resultado.getName());
    }
}
