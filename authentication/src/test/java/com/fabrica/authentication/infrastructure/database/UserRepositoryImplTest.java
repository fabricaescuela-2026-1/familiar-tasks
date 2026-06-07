package com.fabrica.authentication.infrastructure.database;

import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import com.fabrica.authentication.infrastructure.database.entities.mappers.UserEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.UserJpaRepository;
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
class UserRepositoryImplTest {

    @Mock private UserJpaRepository userRepo;
    @Mock private UserEntityMapper userEntityMapper;

    @InjectMocks private UserRepositoryImpl repository;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void findByEmailRetornaUsuarioMapeadoCuandoExiste() {
        // Arrange
        UserEntity entity = UserEntity.builder().userId(UUID.randomUUID()).email("a@mail.com").build();
        User user = User.builder().userId(entity.getUserId()).email("a@mail.com").build();
        when(userRepo.findByEmail("a@mail.com")).thenReturn(Optional.of(entity));
        when(userEntityMapper.toDomain(entity)).thenReturn(user);

        // Act
        Optional<User> result = repository.findByEmail("a@mail.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void saveMapeaPersisteYRetornaDominio() {
        // Arrange
        User user = User.builder().userId(UUID.randomUUID()).email("a@mail.com").build();
        UserEntity entity = UserEntity.builder().userId(user.getUserId()).email("a@mail.com").build();
        when(userEntityMapper.toEntity(user)).thenReturn(entity);
        when(userRepo.save(entity)).thenReturn(entity);
        when(userEntityMapper.toDomain(entity)).thenReturn(user);

        // Act
        User result = repository.save(user);

        // Assert
        assertEquals(user, result);
        verify(userRepo).save(entity);
    }

    @Test
    void activateUserByEmailDelegaAlJpaRepository() {
        repository.activateUserByEmail("a@mail.com");
        verify(userRepo).activateUserByEmail("a@mail.com");
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void findByEmailRetornaVacioCuandoNoExiste() {
        when(userRepo.findByEmail("x@mail.com")).thenReturn(Optional.empty());
        assertTrue(repository.findByEmail("x@mail.com").isEmpty());
        verifyNoInteractions(userEntityMapper);
    }
}
