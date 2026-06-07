package com.fabrica.authentication.infrastructure.database;

import com.fabrica.authentication.domain.model.ActivationToken;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.database.entities.ActivationTokenEntity;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import com.fabrica.authentication.infrastructure.database.entities.mappers.ActivationTokenMapper;
import com.fabrica.authentication.infrastructure.database.entities.mappers.UserEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.ActivationTokenJpaRepository;
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
class ActivationTokenRepositoryImplTest {

    @Mock private ActivationTokenJpaRepository tokenRepo;
    @Mock private ActivationTokenMapper tokenMapper;
    @Mock private UserEntityMapper userMapper;

    @InjectMocks private ActivationTokenRepositoryImpl repository;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void saveMapeaYDelegaAlJpaRepository() {
        // Arrange
        ActivationToken token = ActivationToken.builder().id(UUID.randomUUID()).build();
        User user = User.builder().email("a@mail.com").build();
        UserEntity userEntity = UserEntity.builder().email("a@mail.com").build();
        ActivationTokenEntity entity = ActivationTokenEntity.builder().id(token.getId()).build();
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(tokenMapper.mapToEntity(token, userEntity)).thenReturn(entity);

        // Act
        repository.save(token, user);

        // Assert
        verify(tokenRepo).save(entity);
    }

    @Test
    void invalidateAllByUserEmailDelegaAlJpaRepository() {
        // Act
        repository.invalidateAllByUserEmail("a@mail.com");

        // Assert
        verify(tokenRepo).invalidateAllByUserEmail("a@mail.com");
    }

    @Test
    void findLastByUserEmailRetornaTokenMapeadoCuandoExiste() {
        // Arrange
        ActivationTokenEntity entity = ActivationTokenEntity.builder().id(UUID.randomUUID()).build();
        ActivationToken token = ActivationToken.builder().id(entity.getId()).build();
        when(tokenRepo.findLastByUserEmail("a@mail.com")).thenReturn(Optional.of(entity));
        when(tokenMapper.mapToActivationToken(entity)).thenReturn(token);

        // Act
        Optional<ActivationToken> result = repository.findLastByUserEmail("a@mail.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    void findLastByUserEmailRetornaVacioCuandoNoExiste() {
        // Arrange
        when(tokenRepo.findLastByUserEmail("a@mail.com")).thenReturn(Optional.empty());

        // Act
        Optional<ActivationToken> result = repository.findLastByUserEmail("a@mail.com");

        // Assert
        assertTrue(result.isEmpty());
        verifyNoInteractions(tokenMapper);
    }

    @Test
    void increaseAttemptsByOneDelegaAlJpaRepository() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        repository.increaseAttemptsByOne(id);

        // Assert
        verify(tokenRepo).increaseAttemptsByOne(id);
    }
}
