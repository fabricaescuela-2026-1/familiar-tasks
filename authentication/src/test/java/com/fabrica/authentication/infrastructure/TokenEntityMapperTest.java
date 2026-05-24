package com.fabrica.authentication.infrastructure;

import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.infrastructure.database.entities.TokenEntity;
import com.fabrica.authentication.infrastructure.database.entities.TokenType;
import com.fabrica.authentication.infrastructure.database.entities.UserEntity;
import com.fabrica.authentication.infrastructure.database.entities.mappers.TokenEntityMapper;
import com.fabrica.authentication.infrastructure.database.entities.mappers.UserEntityMapper;
import com.fabrica.authentication.infrastructure.database.jpa.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenEntityMapperTest {

    @Mock private UserJpaRepository userJpaRepo;
    @Mock private UserEntityMapper userEntityMapper;

    @InjectMocks
    private TokenEntityMapper mapper;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toEntityConvierteTokenADominio() {
        // Arrange
        UUID tokenId = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        User user = User.builder().email("carlos@mail.com").build();
        UserEntity userEntity = UserEntity.builder().email("carlos@mail.com").build();
        Token token = Token.builder()
            .tokenId(tokenId)
            .tokenHash("hash-123")
            .expirationDate(ahora.plusHours(1))
            .expiratedAt(null)
            .tokenType("ACCESS")
            .user(user)
            .build();
        when(userJpaRepo.findByEmail("carlos@mail.com")).thenReturn(Optional.of(userEntity));

        // Act
        TokenEntity entity = mapper.toEntity(token);

        // Assert
        assertEquals(tokenId, entity.getTokenId());
        assertEquals("hash-123", entity.getTokenHash());
        assertEquals(TokenType.ACCESS, entity.getTokenType());
        assertEquals(userEntity, entity.getUser());
    }

    @Test
    void toDomainConvierteEntidadADominio() {
        // Arrange
        UUID tokenId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder().email("ana@mail.com").build();
        User user = User.builder().email("ana@mail.com").build();
        TokenEntity entity = TokenEntity.builder()
            .tokenId(tokenId)
            .tokenHash("hash-abc")
            .expirationDate(LocalDateTime.now().plusHours(2))
            .tokenType(TokenType.REFRESH)
            .user(userEntity)
            .build();
        when(userEntityMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        Token token = mapper.toDomain(entity);

        // Assert
        assertEquals(tokenId, token.getTokenId());
        assertEquals("hash-abc", token.getTokenHash());
        assertEquals("REFRESH", token.getTokenType());
        assertEquals(user, token.getUser());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void toEntityConUsuarioInexistenteLanzaExcepcion() {
        // Arrange
        User user = User.builder().email("noexiste@mail.com").build();
        Token token = Token.builder()
            .tokenHash("hash")
            .tokenType("ACCESS")
            .expirationDate(LocalDateTime.now().plusHours(1))
            .user(user)
            .build();
        when(userJpaRepo.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(UserNotFoundException.class, () -> mapper.toEntity(token));
    }
}
