package com.fabricaescuela.tasks.infraestructure.adapter.out;

import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;
import com.fabricaescuela.tasks.infraestructure.database.jpa.UserRepository;
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
class UserLookupAdapterTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserLookupAdapter adapter;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void findUserIdByUsernameRetornaIdCuandoSeEncuentraPorUsername() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity user = UserEntity.builder().userId(id).build();
        when(userRepository.findUserEntityByUsername("ana")).thenReturn(Optional.of(user));

        // Act
        Optional<UUID> result = adapter.findUserIdByUsername("ana");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void findUserIdByUsernameCaeAFindByEmailCuandoUsernameNoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity user = UserEntity.builder().userId(id).build();
        when(userRepository.findUserEntityByUsername("ana@mail.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ana@mail.com")).thenReturn(Optional.of(user));

        // Act
        Optional<UUID> result = adapter.findUserIdByUsername("ana@mail.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get());
    }

    @Test
    void findUserIdByUsernameCaeAFindByEmailCuandoRepoLanzaRuntime() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity user = UserEntity.builder().userId(id).build();
        when(userRepository.findUserEntityByUsername("ana")).thenThrow(new RuntimeException("boom"));
        when(userRepository.findByEmail("ana")).thenReturn(Optional.of(user));

        // Act
        Optional<UUID> result = adapter.findUserIdByUsername("ana");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void findUserIdByUsernameRetornaVacioCuandoUsernameEsNull() {
        assertTrue(adapter.findUserIdByUsername(null).isEmpty());
        verifyNoInteractions(userRepository);
    }

    @Test
    void findUserIdByUsernameRetornaVacioCuandoUsernameEsBlank() {
        assertTrue(adapter.findUserIdByUsername("   ").isEmpty());
        verifyNoInteractions(userRepository);
    }

    @Test
    void findUserIdByUsernameRetornaVacioCuandoNoEncuentraEnNingunRepo() {
        when(userRepository.findUserEntityByUsername("x")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("x")).thenReturn(Optional.empty());
        assertTrue(adapter.findUserIdByUsername("x").isEmpty());
    }
}
