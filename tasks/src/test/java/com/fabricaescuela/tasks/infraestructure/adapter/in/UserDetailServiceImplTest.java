package com.fabricaescuela.tasks.infraestructure.adapter.in;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;
import com.fabricaescuela.tasks.infraestructure.database.jpa.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserDetailServiceImpl service;

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void loadUserByUsernameExistenteRetornaUserDetails() {
    // Arrange
    UserEntity entity = UserEntity.builder()
      .userId(UUID.randomUUID())
      .email("ana@mail.com")
      .passwordHash("hashed")
      .isActive(true)
      .build();
    when(userRepository.findByEmail("ana@mail.com")).thenReturn(
      Optional.of(entity)
    );

    // Act
    UserDetails details = service.loadUserByUsername("ana@mail.com");

    // Assert
    assertEquals("ana@mail.com", details.getUsername());
    assertEquals("hashed", details.getPassword());
    assertTrue(details.isEnabled());
    assertTrue(
      details
        .getAuthorities()
        .stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
    );
  }

  // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

  @Test
  void loadUserByUsernameInexistenteLanzaUsernameNotFoundException() {
    // Arrange
    when(userRepository.findByEmail("noexiste@mail.com")).thenReturn(
      Optional.empty()
    );

    // Act - Assert
    assertThrows(UsernameNotFoundException.class, () ->
      service.loadUserByUsername("noexiste@mail.com")
    );
  }
}
