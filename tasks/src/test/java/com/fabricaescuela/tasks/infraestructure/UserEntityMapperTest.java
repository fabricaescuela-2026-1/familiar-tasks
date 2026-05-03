package com.fabricaescuela.tasks.infraestructure;

import com.fabricaescuela.tasks.domain.model.User;
import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;
import com.fabricaescuela.tasks.infraestructure.database.mappers.UserEntityMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityMapperTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

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
        User user = UserEntityMapper.toDomain(entity);

        // Assert
        assertEquals(id,                user.getUserId());
        assertEquals("Carlos",          user.getName());
        assertEquals("Ruiz",            user.getLastname());
        assertEquals("carlos@mail.com", user.getEmail());
        assertEquals("hash-pass",       user.getPasswordHash());
        assertTrue(user.isActive());
    }

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
        UserEntity entity = UserEntityMapper.toEntity(user);

        // Assert
        assertEquals(id,                entity.getUserId());
        assertEquals("Carlos",          entity.getName());
        assertEquals("Ruiz",            entity.getLastname());
        assertEquals("carlos@mail.com", entity.getEmail());
        assertTrue(entity.isActive());
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
        User resultado = UserEntityMapper.toDomain(UserEntityMapper.toEntity(original));

        // Assert
        assertEquals(original.getUserId(), resultado.getUserId());
        assertEquals(original.getEmail(),  resultado.getEmail());
    }
}
