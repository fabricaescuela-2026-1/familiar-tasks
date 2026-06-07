package com.fabrica.authentication.infrastructure.database;

import com.fabrica.authentication.domain.model.TwoFactorAuthToken;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.database.entities.TwoFactorAuthTokenEntity;
import com.fabrica.authentication.infrastructure.database.entities.mappers.TwoFactorAuthTokenEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.TwoFactorAuthTokenJpaRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthTokenRepoImplTest {

    @Mock private TwoFactorAuthTokenJpaRepo authTokenRepo;
    @Mock private TwoFactorAuthTokenEntityMapper authTokenMapper;

    @InjectMocks private TwoFactorAuthTokenRepoImpl repository;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void saveMapeaYDelegaAlJpaRepository() {
        // Arrange
        TwoFactorAuthToken token = TwoFactorAuthToken.builder()
            .id(UUID.randomUUID())
            .user(User.builder().email("a@mail.com").build())
            .build();
        TwoFactorAuthTokenEntity entity = TwoFactorAuthTokenEntity.builder().id(token.getId()).build();
        when(authTokenMapper.toEntity(token)).thenReturn(entity);

        // Act
        repository.save(token);

        // Assert
        verify(authTokenRepo).save(entity);
    }

    @Test
    void invalidateAllByUserEmailDelegaAlJpaRepository() {
        repository.invalidateAllByUserEmail("a@mail.com");
        verify(authTokenRepo).invalidateAllByUserEmail("a@mail.com");
    }

    @Test
    void findLastByUserEmailRetornaTokenMapeadoCuandoExiste() {
        // Arrange
        TwoFactorAuthTokenEntity entity = TwoFactorAuthTokenEntity.builder().id(UUID.randomUUID()).build();
        TwoFactorAuthToken token = TwoFactorAuthToken.builder().id(entity.getId()).build();
        when(authTokenRepo.findLastByUserEmail("a@mail.com")).thenReturn(Optional.of(entity));
        when(authTokenMapper.toDomain(entity)).thenReturn(token);

        // Act
        Optional<TwoFactorAuthToken> result = repository.findLastByUserEmail("a@mail.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    void findLastByUserEmailRetornaVacioCuandoNoExiste() {
        when(authTokenRepo.findLastByUserEmail("a@mail.com")).thenReturn(Optional.empty());
        assertTrue(repository.findLastByUserEmail("a@mail.com").isEmpty());
        verifyNoInteractions(authTokenMapper);
    }

    @Test
    void increaseAttemptsByOneDelegaAlJpaRepository() {
        UUID id = UUID.randomUUID();
        repository.increaseAttemptsByOne(id);
        verify(authTokenRepo).increaseAttemptsByOne(id);
    }
}
