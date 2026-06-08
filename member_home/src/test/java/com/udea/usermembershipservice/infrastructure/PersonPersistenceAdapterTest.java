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

import com.udea.usermembershipservice.domain.model.Person;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out.PersonPersistenceAdapter;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.PersonJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.PersonPersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataJpaRepository;

@ExtendWith(MockitoExtension.class)
class PersonPersistenceAdapterTest {

  @Mock private SpringDataJpaRepository repository;

  private PersonPersistenceAdapter adapter;
  private final PersonPersistenceMapper mapper = new PersonPersistenceMapper();

  @BeforeEach
  void setUp() {
    adapter = new PersonPersistenceAdapter(repository, mapper);
  }

  private Person personaValida() {
    return Person.restore(
        UUID.randomUUID(), "Carlos", "Ruiz", "carlos@familia.com",
        "hash", LocalDateTime.of(2026, 1, 1, 10, 0, 0), true);
  }

  private PersonJpaEntity entidadValida(UUID id) {
    return new PersonJpaEntity(
        id, "Carlos", "Ruiz", "carlos@familia.com",
        "hash", LocalDateTime.of(2026, 1, 1, 10, 0, 0), true);
  }

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void guardarPersonaPersisteEntidad() {
    // Arrange
    Person person = personaValida();
    when(repository.save(any(PersonJpaEntity.class)))
        .thenReturn(entidadValida(person.getIdPerson()));

    // Act
    adapter.saveUser(person);

    // Assert
    verify(repository, times(1)).save(any(PersonJpaEntity.class));
  }

  @Test
  void obtenerPersonaPorEmailExistenteLaRetorna() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findByEmail("carlos@familia.com"))
        .thenReturn(Optional.of(entidadValida(id)));

    // Act
    Optional<Person> person = adapter.getUserByEmail("carlos@familia.com");

    // Assert
    assertTrue(person.isPresent());
    assertEquals(id, person.get().getIdPerson());
  }

  @Test
  void obtenerPersonaPorIdExistenteLaRetorna() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.of(entidadValida(id)));

    // Act
    Optional<Person> person = adapter.getUserById(id);

    // Assert
    assertTrue(person.isPresent());
    assertEquals("Carlos", person.get().getName());
  }

  @Test
  void obtenerTodasLasPersonasRetornaListaMapeada() {
    // Arrange
    PersonJpaEntity e1 = entidadValida(UUID.randomUUID());
    PersonJpaEntity e2 = entidadValida(UUID.randomUUID());
    when(repository.findAll()).thenReturn(List.of(e1, e2));

    // Act
    List<Person> personas = adapter.getAllUsers();

    // Assert
    assertEquals(2, personas.size());
  }

  // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

  @Test
  void guardarPersonaFallaSiRepositorioRetornaNull() {
    // Arrange
    Person person = personaValida();
    when(repository.save(any(PersonJpaEntity.class))).thenReturn(null);

    // Act - Assert
    assertThrows(IllegalStateException.class, () -> adapter.saveUser(person));
  }
}
