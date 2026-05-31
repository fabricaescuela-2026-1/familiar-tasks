package com.udea.usermembershipservice.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.udea.usermembershipservice.domain.model.Home;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out.HomePersistenceAdapter;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.HomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.HomePersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataHomeJpaRepository;

@ExtendWith(MockitoExtension.class)
class HomePersistenceAdapterTest {

  @Mock private SpringDataHomeJpaRepository repository;

  private HomePersistenceAdapter adapter;
  private final HomePersistenceMapper mapper = new HomePersistenceMapper();

  @BeforeEach
  void setUp() {
    adapter = new HomePersistenceAdapter(repository, mapper);
  }

  private Home hogarValido() {
    return Home.create(UUID.randomUUID(), "Los García", LocalDateTime.now());
  }

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void guardarHogarPersisteEntidad() {
    // Arrange
    Home home = hogarValido();
    when(repository.save(any(HomeJpaEntity.class)))
        .thenReturn(new HomeJpaEntity(home.getIdHome(), home.getName(), home.getCreatedAt()));

    // Act
    adapter.saveHome(home);

    // Assert
    verify(repository, times(1)).save(any(HomeJpaEntity.class));
  }

  @Test
  void obtenerTodosLosHogaresRetornaListaMapeada() {
    // Arrange
    HomeJpaEntity e1 = new HomeJpaEntity(UUID.randomUUID(), "Los García", LocalDateTime.now());
    HomeJpaEntity e2 = new HomeJpaEntity(UUID.randomUUID(), "Los Pérez", LocalDateTime.now());
    when(repository.findAll()).thenReturn(List.of(e1, e2));

    // Act
    List<Home> homes = adapter.getAllHomes();

    // Assert
    assertEquals(2, homes.size());
    assertEquals("Los García", homes.get(0).getName());
    assertEquals("Los Pérez", homes.get(1).getName());
  }

  @Test
  void obtenerHogarPorNombreExistenteLoRetorna() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findByNameIgnoreCase("Los García"))
        .thenReturn(Optional.of(new HomeJpaEntity(id, "Los García", LocalDateTime.now())));

    // Act
    Optional<Home> home = adapter.getHomeByName("Los García");

    // Assert
    assertTrue(home.isPresent());
    assertEquals(id, home.get().getIdHome());
  }

  @Test
  void obtenerHogarPorIdExistenteLoRetorna() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findById(id))
        .thenReturn(Optional.of(new HomeJpaEntity(id, "Los García", LocalDateTime.now())));

    // Act
    Optional<Home> home = adapter.getHomeById(id);

    // Assert
    assertTrue(home.isPresent());
    assertEquals("Los García", home.get().getName());
  }

  @Test
  void eliminarHogarExistenteInvocaDeleteById() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findByNameIgnoreCase("Los García"))
        .thenReturn(Optional.of(new HomeJpaEntity(id, "Los García", LocalDateTime.now())));

    // Act
    adapter.deleteHome("Los García");

    // Assert
    verify(repository, times(1)).deleteById(id);
  }

  // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

  @Test
  void guardarHogarFallaSiRepositorioRetornaNull() {
    // Arrange
    Home home = hogarValido();
    when(repository.save(any(HomeJpaEntity.class))).thenReturn(null);

    // Act - Assert
    assertThrows(IllegalStateException.class, () -> adapter.saveHome(home));
  }

  @Test
  void eliminarHogarInexistenteLanzaExcepcion() {
    // Arrange
    when(repository.findByNameIgnoreCase("NO_EXISTE")).thenReturn(Optional.empty());

    // Act - Assert
    assertThrows(RuntimeException.class, () -> adapter.deleteHome("NO_EXISTE"));
  }
}
