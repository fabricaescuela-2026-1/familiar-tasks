package com.fabrica.authentication.infrastructure.database;

import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.database.entities.TokenEntity;
import com.fabrica.authentication.infrastructure.database.entities.mappers.TokenEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.TokenJpaRepository;
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
class TokenRepositoryImplTest {

    @Mock private TokenJpaRepository tokenJpaRepo;
    @Mock private TokenEntityMapper tokenEntityMapper;

    @InjectMocks private TokenRepositoryImpl repository;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void saveMapeaPersisteYRetornaDominio() {
        // Arrange
        Token token = Token.builder().tokenId(UUID.randomUUID())
            .user(User.builder().email("a@mail.com").build()).build();
        TokenEntity entity = TokenEntity.builder().tokenId(token.getTokenId()).build();
        when(tokenEntityMapper.toEntity(token)).thenReturn(entity);
        when(tokenJpaRepo.save(entity)).thenReturn(entity);
        when(tokenEntityMapper.toDomain(entity)).thenReturn(token);

        // Act
        Token result = repository.save(token);

        // Assert
        assertEquals(token, result);
        verify(tokenJpaRepo).save(entity);
    }

    @Test
    void revokeAllByUserEmailDelegaAlJpaRepository() {
        repository.revokeAllByUserEmail("a@mail.com");
        verify(tokenJpaRepo).revokeAllByUserEmail("a@mail.com");
    }

    @Test
    void findByHashRetornaTokenMapeadoCuandoExiste() {
        // Arrange
        TokenEntity entity = TokenEntity.builder().tokenId(UUID.randomUUID()).tokenHash("h").build();
        Token token = Token.builder().tokenId(entity.getTokenId()).tokenHash("h").build();
        when(tokenJpaRepo.findByTokenHash("h")).thenReturn(Optional.of(entity));
        when(tokenEntityMapper.toDomain(entity)).thenReturn(token);

        // Act
        Optional<Token> result = repository.findByHash("h");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void findByHashRetornaVacioCuandoNoExiste() {
        when(tokenJpaRepo.findByTokenHash("h")).thenReturn(Optional.empty());
        assertTrue(repository.findByHash("h").isEmpty());
        verifyNoInteractions(tokenEntityMapper);
    }
}
