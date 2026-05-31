package com.udea.usermembershipservice.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.udea.usermembershipservice.domain.model.Role;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out.RolePersistenceAdapter;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.RolePersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataRoleJpaRepository;

@ExtendWith(MockitoExtension.class)
class RolePersistenceAdapterTest {

  @Mock private SpringDataRoleJpaRepository repository;

  private RolePersistenceAdapter adapter;
  private final RolePersistenceMapper mapper = new RolePersistenceMapper();

  @BeforeEach
  void setUp() {
    adapter = new RolePersistenceAdapter(repository, mapper);
  }

  private Role rolValido() {
    return Role.create(UUID.randomUUID(), "ADMIN");
  }

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void guardarRolPersisteEntidad() {
    // Arrange
    Role role = rolValido();
    when(repository.save(any(RoleJpaEntity.class)))
        .thenReturn(new RoleJpaEntity(role.getIdRole(), role.getName()));

    // Act
    adapter.saveRole(role);

    // Assert
    verify(repository, times(1)).save(any(RoleJpaEntity.class));
  }

  @Test
  void obtenerTodosLosRolesRetornaListaMapeada() {
    // Arrange
    RoleJpaEntity e1 = new RoleJpaEntity(UUID.randomUUID(), "ADMIN");
    RoleJpaEntity e2 = new RoleJpaEntity(UUID.randomUUID(), "MEMBER");
    when(repository.findAll()).thenReturn(List.of(e1, e2));

    // Act
    List<Role> roles = adapter.getAllRoles();

    // Assert
    assertEquals(2, roles.size());
    assertEquals("ADMIN", roles.get(0).getName());
    assertEquals("MEMBER", roles.get(1).getName());
  }

  @Test
  void obtenerRolPorNombreExistenteLoRetorna() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findByNameIgnoreCase("ADMIN"))
        .thenReturn(Optional.of(new RoleJpaEntity(id, "ADMIN")));

    // Act
    Optional<Role> role = adapter.getRoleByName("ADMIN");

    // Assert
    assertTrue(role.isPresent());
    assertEquals(id, role.get().getIdRole());
  }

  @Test
  void obtenerRolPorIdExistenteLoRetorna() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findById(id))
        .thenReturn(Optional.of(new RoleJpaEntity(id, "ADMIN")));

    // Act
    Optional<Role> role = adapter.getRoleById(id);

    // Assert
    assertTrue(role.isPresent());
    assertEquals("ADMIN", role.get().getName());
  }

  @Test
  void eliminarRolExistenteInvocaDeleteById() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findByNameIgnoreCase("ADMIN"))
        .thenReturn(Optional.of(new RoleJpaEntity(id, "ADMIN")));

    // Act
    adapter.deleteRole("ADMIN");

    // Assert
    verify(repository, times(1)).deleteById(id);
  }

  @Test
  void obtenerNombreRolPorIdExistenteRetornaNombre() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findById(id))
        .thenReturn(Optional.of(new RoleJpaEntity(id, "ADMIN")));

    // Act
    String name = adapter.getRoleNameById(id);

    // Assert
    assertEquals("ADMIN", name);
  }

  // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

  @Test
  void guardarRolFallaSiRepositorioRetornaNull() {
    // Arrange
    Role role = rolValido();
    when(repository.save(any(RoleJpaEntity.class))).thenReturn(null);

    // Act - Assert
    assertThrows(IllegalStateException.class, () -> adapter.saveRole(role));
  }

  @Test
  void eliminarRolInexistenteLanzaExcepcion() {
    // Arrange
    when(repository.findByNameIgnoreCase("NO_EXISTE")).thenReturn(Optional.empty());

    // Act - Assert
    assertThrows(RuntimeException.class, () -> adapter.deleteRole("NO_EXISTE"));
  }

  @Test
  void obtenerNombreRolPorIdInexistenteLanzaExcepcion() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.empty());

    // Act - Assert
    assertThrows(RuntimeException.class, () -> adapter.getRoleNameById(id));
  }
}
