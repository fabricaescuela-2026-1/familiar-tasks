package com.fabrica.authentication.infrastructure;

import com.fabrica.authentication.domain.ports.out.UserRepositoryPort;
import com.fabrica.authentication.infrastructure.web.config.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock private UserRepositoryPort userRepo;

    @InjectMocks
    private UserDetailsServiceImpl service;

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void loadUserByUsernameInexistenteLanzaRuntimeException() {
        // Arrange
        when(userRepo.findByEmail("nope@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> service.loadUserByUsername("nope@mail.com"));
        assertEquals("User not found", ex.getMessage());
    }
}
