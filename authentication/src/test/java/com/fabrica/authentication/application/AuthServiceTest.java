package com.fabrica.authentication.application;

import com.fabrica.authentication.application.dto.AuthResponse;
import com.fabrica.authentication.application.dto.LoginRequest;
import com.fabrica.authentication.application.dto.RegisterRequest;
import com.fabrica.authentication.application.dto.TokenResponse;
import com.fabrica.authentication.domain.exceptions.EmailAlreadyExitsException;
import com.fabrica.authentication.domain.exceptions.InvalidRefreshTokenException;
import com.fabrica.authentication.domain.exceptions.InvalidTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.Token;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.application.ports.out.UserQueuePort;
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
    @Mock private UserQueuePort userQueuePort;

    @InjectMocks
    private AuthService authService;

    private Token tokenFalso(String hash) {
        return Token.builder().tokenHash(hash).build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void registroExitosoConEmailNuevo() {
        // Arrange
        var request = new RegisterRequest("Carlos", "Ruiz", "carlos@mail.com", "Segura@123");
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
        var request = new RegisterRequest("Carlos", "Ruiz", "carlos@mail.com", "Segura@123");
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

    // HU01 Scenario 3
    @Test
    void registroConContrasenaDebilEsRechazado() {
        // Arrange
        var request = new RegisterRequest("Carlos", "Ruiz", "carlos@mail.com", "abc");

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepo, never()).save(any());
    }

    // HU01 Scenario 4
    @Test
    void registroConNombreVacioEsRechazado() {
        // Arrange
        var request = new RegisterRequest("", "Ruiz", "carlos@mail.com", "Segura@123");

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepo, never()).save(any());
    }

    // HU01 Scenario 4 — email es campo obligatorio
    @Test
    void registroConEmailNuloEsRechazado() {
        // Arrange
        var request = new RegisterRequest("Carlos", "Ruiz", null, "Segura@123");

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepo, never()).save(any());
    }

    // HU01 Scenario 4 — apellido es campo obligatorio
    @Test
    void registroConApellidoVacioEsRechazado() {
        // Arrange
        var request = new RegisterRequest("Carlos", "", "carlos@mail.com", "Segura@123");

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepo, never()).save(any());
    }

    // HU01/HU02 Scenario 3 — contraseña sin caracteres especiales debe ser rechazada
    @Test
    void registroConContrasenaSinCaracterEspecialEsRechazado() {
        // Arrange
        var request = new RegisterRequest("Carlos", "Ruiz", "carlos@mail.com", "sinEspecial8");

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepo, never()).save(any());
    }

    // HU02 Scenario 1 — contraseña sin mayúscula debe ser rechazada
    @Test
    void registroConContrasenaSinMayusculaEsRechazado() {
        // Arrange
        var request = new RegisterRequest("Carlos", "Ruiz", "carlos@mail.com", "segura@123");

        // Act - Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepo, never()).save(any());
    }

    // HU01 — obtener token activo por hash
    @Test
    void obtenerTokenPorHashExistenteRetornaTokenResponse() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID tokenId = UUID.randomUUID();
        var user = User.builder().userId(userId).build();
        var token = Token.builder()
            .tokenId(tokenId)
            .tokenHash("valid-hash")
            .tokenType("ACCESS")
            .user(user)
            .build();
        when(tokenRepo.findByHash("valid-hash")).thenReturn(Optional.of(token));

        // Act
        TokenResponse response = authService.getToken("valid-hash");

        // Assert
        assertEquals(tokenId,      response.tokenId());
        assertEquals("valid-hash", response.tokenHash());
        assertEquals(userId,       response.userId());
    }

    @Test
    void obtenerTokenPorHashInexistenteLanzaExcepcion() {
        // Arrange
        when(tokenRepo.findByHash("no-existe")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(InvalidTokenException.class, () ->
            authService.getToken("no-existe")
        );
    }

    // HU04 Scenario 2 — credenciales en blanco también son inválidas
    @Test
    void loginConEmailVacioEsRechazado() {
        // Arrange
        var request = new LoginRequest("", "pass1234");
        when(userRepo.findByEmail("")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void loginConPasswordVaciaEsRechazado() {
        // Arrange
        var user = User.builder().email("juan@mail.com").passwordHash("hashed").build();
        var request = new LoginRequest("juan@mail.com", "");
        when(userRepo.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("", "hashed")).thenReturn(false);

        // Act - Assert
        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    // HU04 Scenario 3 — un token cuya fecha de expiración ya pasó no debe ser válido
    @Test
    void obtenerTokenExpiradoLanzaExcepcion() {
        // Arrange
        var user = User.builder().userId(UUID.randomUUID()).email("pepe@mail.com").build();
        var tokenExpirado = Token.builder()
            .tokenHash("expirado-hash")
            .expirationDate(java.time.LocalDateTime.now().minusMinutes(1))
            .tokenType("ACCESS")
            .user(user)
            .build();
        when(tokenRepo.findByHash("expirado-hash")).thenReturn(Optional.of(tokenExpirado));

        // Act - Assert
        assertThrows(InvalidTokenException.class, () -> authService.getToken("expirado-hash"));
    }

    // HU05 — al renovar, se emite un refresh token nuevo distinto al anterior (rotación)
    @Test
    void refreshTokenValidoRotaRefreshToken() {
        // Arrange
        var user = User.builder().userId(UUID.randomUUID()).build();
        var refresh = Token.builder().tokenHash("refresh-original").user(user).build();
        when(tokenRepo.findByHash("refresh-original")).thenReturn(Optional.of(refresh));
        when(jwtService.isTokenValid(refresh)).thenReturn(true);
        when(jwtService.generateAccesToken(user)).thenReturn(tokenFalso("access-nuevo"));

        // Act
        AuthResponse response = authService.refreshToken("refresh-original");

        // Assert
        assertNotEquals("refresh-original", response.refreshToken());
    }

    // HU05 Scenario 2 — refresh con hash nulo no debe permitir renovación
    @Test
    void refreshTokenNuloLanzaExcepcion() {
        // Arrange
        when(tokenRepo.findByHash(null)).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(null));
    }

    // HU06 Scenario 2 — un token revocado no debe poder usarse para obtener datos
    @Test
    void obtenerTokenRevocadoLanzaExcepcion() {
        // Arrange
        var user = User.builder().email("carlos@mail.com").build();
        var tokenRevocado = Token.builder()
            .tokenHash("revocado-hash")
            .expirationDate(java.time.LocalDateTime.now().plusDays(1))
            .expiratedAt(java.time.LocalDateTime.now().minusMinutes(1))
            .user(user)
            .build();
        when(tokenRepo.findByHash("revocado-hash")).thenReturn(Optional.of(tokenRevocado));

        // Act - Assert
        assertThrows(InvalidTokenException.class, () -> authService.getToken("revocado-hash"));
    }
}
