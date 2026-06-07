package com.fabrica.authentication.infrastructure.database.entities.mappers;

import com.fabrica.authentication.domain.model.ActivationToken;
import com.fabrica.authentication.infrastructure.database.entities.ActivationTokenEntity;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActivationTokenMapperTest {

    private final ActivationTokenMapper mapper = new ActivationTokenMapper();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void mapToEntityConstruyeEntidadConTimestampsYUsuario() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity user = UserEntity.builder().userId(UUID.randomUUID()).email("a@mail.com").build();
        ActivationToken token = ActivationToken.builder().id(id).codeHash("h").build();

        // Act
        ActivationTokenEntity entity = mapper.mapToEntity(token, user);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals("h", entity.getCodeHash());
        assertEquals(user, entity.getUser());
        assertEquals(0, entity.getAttempts());
        assertFalse(entity.isInvalidated());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getExpiresAt());
        assertTrue(entity.getExpiresAt().isAfter(entity.getCreatedAt()));
    }

    @Test
    void mapToActivationTokenConstruyeDominioDesdeEntidad() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ActivationTokenEntity entity = ActivationTokenEntity.builder()
            .id(id)
            .codeHash("h")
            .createdAt(now)
            .expiresAt(now.plusMinutes(10))
            .attempts(2)
            .invalidated(true)
            .build();

        // Act
        ActivationToken token = mapper.mapToActivationToken(entity);

        // Assert
        assertEquals(id, token.getId());
        assertEquals("h", token.getCodeHash());
        assertEquals(now, token.getCreatedAt());
        assertEquals(2, token.getAttempts());
        assertTrue(token.isInvalidated());
    }
}
