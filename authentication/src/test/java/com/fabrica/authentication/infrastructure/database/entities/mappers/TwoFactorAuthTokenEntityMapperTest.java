package com.fabrica.authentication.infrastructure.database.entities.mappers;

import com.fabrica.authentication.domain.model.TwoFactorAuthToken;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.database.entities.TwoFactorAuthTokenEntity;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthTokenEntityMapperTest {

    @Mock private UserEntityMapper userEntityMapper;

    @InjectMocks private TwoFactorAuthTokenEntityMapper mapper;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toEntityConvierteDominioAEntidad() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = User.builder().email("a@mail.com").build();
        UserEntity userEntity = UserEntity.builder().email("a@mail.com").build();
        LocalDateTime now = LocalDateTime.now();
        TwoFactorAuthToken token = TwoFactorAuthToken.builder()
            .id(id).codeHash("h").user(user)
            .createdAt(now).expiresAt(now.plusMinutes(5))
            .attempts(1).invalidated(false).build();
        when(userEntityMapper.toEntity(user)).thenReturn(userEntity);

        // Act
        TwoFactorAuthTokenEntity entity = mapper.toEntity(token);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals("h", entity.getCodeHash());
        assertEquals(userEntity, entity.getUser());
        assertEquals(1, entity.getAttempts());
        assertFalse(entity.isInvalidated());
    }

    @Test
    void toDomainConvierteEntidadADominio() {
        // Arrange
        UserEntity userEntity = UserEntity.builder().email("a@mail.com").build();
        User user = User.builder().email("a@mail.com").build();
        LocalDateTime now = LocalDateTime.now();
        TwoFactorAuthTokenEntity entity = TwoFactorAuthTokenEntity.builder()
            .id(UUID.randomUUID()).codeHash("h").user(userEntity)
            .createdAt(now).expiresAt(now.plusMinutes(5))
            .attempts(0).invalidated(true).build();
        when(userEntityMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        TwoFactorAuthToken token = mapper.toDomain(entity);

        // Assert
        assertEquals(user, token.getUser());
        assertEquals("h", token.getCodeHash());
        assertTrue(token.isInvalidated());
    }
}
