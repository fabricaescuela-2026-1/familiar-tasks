package com.fabrica.authentication.application;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.domain.exceptions.EmailAlreadyExitsException;
import com.fabrica.authentication.domain.exceptions.InvalidRefreshTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.JwtServicePort;
import com.fabrica.authentication.domain.ports.out.TokenRepositoryPort;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private JwtServicePort jwtService;
    @Mock private TokenRepositoryPort tokenRepo;
    @Mock private UserRepositoryPort userRepo;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Token tokenFalso(String hash) {
        return Token.builder().tokenHash(hash).build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void registroExitosoConEmailNuevo() {
        // Arrange
        var request = new RegisterRequest("Carlos", "Ruiz", "carlos@mail.com", "pass1234");
        when(userRepo.findByEmail("carlos@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hash-password");
        when(jwtService.generateAccesToken(any())).thenReturn(tokenFalso("access-token"));
        when(jwtService.generateRefreshToken(any())).thenReturn(tokenFalso("refresh-token"));

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void loginExitosoConCredencialesValidas() {
        // Arrange
        var user = User.builder().email("laura@mail.com").passwordHash("hashed").build();
        var request = new LoginRequest("laura@mail.com", "pass1234");
        when(userRepo.findByEmail("laura@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass1234", "hashed")).thenReturn(true);
        when(jwtService.generateAccesToken(user)).thenReturn(tokenFalso("access-token"));
        when(jwtService.generateRefreshToken(user)).thenReturn(tokenFalso("refresh-token"));

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
    }

    @Test
    void refreshTokenValidoRetornaNuevoAccessToken() {
        // Arrange
        var user = User.builder().userId(UUID.randomUUID()).build();
        var tokenGuardado = Token.builder().tokenHash("refresh-hash").user(user).build();
        when(tokenRepo.findByHash("refresh-hash")).thenReturn(Optional.of(tokenGuardado));
        when(jwtService.isTokenValid(tokenGuardado)).thenReturn(true);
        when(jwtService.generateAccesToken(user)).thenReturn(tokenFalso("nuevo-access"));

        // Act
        AuthResponse response = authService.refreshToken("refresh-hash");

        // Assert
        assertEquals("nuevo-access", response.accessToken());
        assertEquals("refresh-hash", response.refreshToken());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void registroConEmailExistenteLanzaExcepcion() {
        // Arrange
        var request = new RegisterRequest("Carlos", "Ruiz", "carlos@mail.com", "pass1234");
        when(userRepo.findByEmail("carlos@mail.com")).thenReturn(Optional.of(new User()));

        // Act - Assert
        assertThrows(EmailAlreadyExitsException.class, () -> authService.register(request));
        verify(userRepo, never()).save(any());
    }

    @Test
    void loginConEmailInexistenteLanzaExcepcion() {
        // Arrange
        var request = new LoginRequest("noexiste@mail.com", "pass1234");
        when(userRepo.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void loginConContrasenaIncorrectaLanzaExcepcion() {
        // Arrange
        var user = User.builder().email("laura@mail.com").passwordHash("hashed").build();
        var request = new LoginRequest("laura@mail.com", "incorrecta");
        when(userRepo.findByEmail("laura@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("incorrecta", "hashed")).thenReturn(false);

        // Act - Assert
        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void refreshTokenInexistenteLanzaExcepcion() {
        // Arrange
        when(tokenRepo.findByHash("token-invalido")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(InvalidRefreshTokenException.class,
                () -> authService.refreshToken("token-invalido"));
    }

    @Test
    void refreshTokenExpiradoLanzaExcepcion() {
        // Arrange
        var tokenExpirado = Token.builder().tokenHash("expirado").user(new User()).build();
        when(tokenRepo.findByHash("expirado")).thenReturn(Optional.of(tokenExpirado));
        when(jwtService.isTokenValid(tokenExpirado)).thenReturn(false);

        // Act - Assert
        assertThrows(InvalidRefreshTokenException.class,
                () -> authService.refreshToken("expirado"));
    }
}
