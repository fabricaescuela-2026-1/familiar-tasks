package com.fabrica.authentication.application;

import com.fabrica.authentication.application.dto.ActivationAccountResponse;
import com.fabrica.authentication.application.dto.mail.EmailProperties;
import com.fabrica.authentication.application.ports.out.EmailSendingPort;
import com.fabrica.authentication.domain.exceptions.InvalidActivationTokenException;
import com.fabrica.authentication.domain.exceptions.UserNotFoundException;
import com.fabrica.authentication.domain.model.ActivationToken;
import com.fabrica.authentication.domain.model.User;
import com.fabrica.authentication.domain.ports.out.ActivationTokenRepositoryPort;
import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountValidationServiceTest {

    @Mock private ActivationTokenRepositoryPort activationTokenRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepositoryPort userRepo;
    @Mock private EmailSendingPort emailSendingComp;

    @InjectMocks private AccountValidationService service;

    private User usuarioActivo() {
        return User.builder()
            .userId(UUID.randomUUID())
            .email("ana@mail.com")
            .name("Ana")
            .lastname("Perez")
            .passwordHash("hash")
            .isActive(false)
            .createdAt(LocalDateTime.now())
            .build();
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void createActivationTokenInvalidaTokensYGuardaUnoNuevoYEnviaEmail() {
        // Arrange
        User user = usuarioActivo();
        when(userRepo.findByEmail("ana@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-code");

        // Act
        service.createActivationToken("ana@mail.com");

        // Assert
        verify(activationTokenRepo).invalidateAllByUserEmail("ana@mail.com");
        ArgumentCaptor<ActivationToken> tokenCaptor = ArgumentCaptor.forClass(ActivationToken.class);
        verify(activationTokenRepo).save(tokenCaptor.capture(), eq(user));
        assertEquals("ana@mail.com", tokenCaptor.getValue().getEmail());
        assertEquals("encoded-code", tokenCaptor.getValue().getCodeHash());
        ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
        verify(emailSendingComp).sendCodeEmail(emailCaptor.capture());
        assertEquals("ana@mail.com", emailCaptor.getValue().recipient());
        assertNotNull(emailCaptor.getValue().code());
    }

    @Test
    void activateAccountConCodigoValidoActivaUsuarioYRetornaRespuesta() {
        // Arrange
        User user = usuarioActivo();
        ActivationToken token = ActivationToken.builder()
            .id(UUID.randomUUID())
            .email("ana@mail.com")
            .codeHash("hash-code")
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .attempts(0)
            .invalidated(false)
            .build();
        when(userRepo.findByEmail("ana@mail.com")).thenReturn(Optional.of(user));
        when(activationTokenRepo.findLastByUserEmail("ana@mail.com")).thenReturn(Optional.of(token));
        when(passwordEncoder.matches("123456", "hash-code")).thenReturn(true);

        // Act
        ActivationAccountResponse response = service.activateAccount("ana@mail.com", "123456");

        // Assert
        assertTrue(response.activated());
        assertEquals("ana@mail.com", response.email());
        assertEquals(user.getUserId(), response.userId());
        verify(activationTokenRepo).increaseAttemptsByOne(token.getId());
        verify(userRepo).activateUserByEmail("ana@mail.com");
        verify(activationTokenRepo).invalidateAllByUserEmail("ana@mail.com");
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void createActivationTokenFallaCuandoUsuarioNoExiste() {
        // Arrange
        when(userRepo.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(UserNotFoundException.class,
            () -> service.createActivationToken("noexiste@mail.com"));
        verify(activationTokenRepo, never()).save(any(), any());
        verify(emailSendingComp, never()).sendCodeEmail(any());
    }

    @Test
    void activateAccountFallaCuandoUsuarioNoExiste() {
        // Arrange
        when(userRepo.findByEmail("x@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(UserNotFoundException.class,
            () -> service.activateAccount("x@mail.com", "123456"));
    }

    @Test
    void activateAccountFallaCuandoNoHayTokenParaUsuario() {
        // Arrange
        User user = usuarioActivo();
        when(userRepo.findByEmail("ana@mail.com")).thenReturn(Optional.of(user));
        when(activationTokenRepo.findLastByUserEmail("ana@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(InvalidActivationTokenException.class,
            () -> service.activateAccount("ana@mail.com", "123456"));
    }

    @Test
    void activateAccountFallaCuandoElCodigoNoCoincide() {
        // Arrange
        User user = usuarioActivo();
        ActivationToken token = ActivationToken.builder()
            .id(UUID.randomUUID())
            .email("ana@mail.com")
            .codeHash("hash-code")
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .attempts(0)
            .invalidated(false)
            .build();
        when(userRepo.findByEmail("ana@mail.com")).thenReturn(Optional.of(user));
        when(activationTokenRepo.findLastByUserEmail("ana@mail.com")).thenReturn(Optional.of(token));
        when(passwordEncoder.matches("000000", "hash-code")).thenReturn(false);

        // Act - Assert
        assertThrows(InvalidActivationTokenException.class,
            () -> service.activateAccount("ana@mail.com", "000000"));
        verify(activationTokenRepo).increaseAttemptsByOne(token.getId());
        verify(userRepo, never()).activateUserByEmail(anyString());
    }

    @Test
    void activateAccountFallaCuandoTokenEstaExpirado() {
        // Arrange
        User user = usuarioActivo();
        ActivationToken token = ActivationToken.builder()
            .id(UUID.randomUUID())
            .email("ana@mail.com")
            .codeHash("hash-code")
            .expiresAt(LocalDateTime.now().minusMinutes(1))
            .attempts(0)
            .invalidated(false)
            .build();
        when(userRepo.findByEmail("ana@mail.com")).thenReturn(Optional.of(user));
        when(activationTokenRepo.findLastByUserEmail("ana@mail.com")).thenReturn(Optional.of(token));

        // Act - Assert
        assertThrows(InvalidActivationTokenException.class,
            () -> service.activateAccount("ana@mail.com", "123456"));
        verify(userRepo, never()).activateUserByEmail(anyString());
    }
}
