package com.udea.usermembershipservice.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out.MemberHomePersistenceAdapter;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.HomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntityId;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.PersonJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.MemberHomePersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataHomeJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataMemberHomeJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataRoleJpaRepository;

@ExtendWith(MockitoExtension.class)
class MemberHomePersistenceAdapterTest {

  @Mock private SpringDataMemberHomeJpaRepository repository;
  @Mock private SpringDataJpaRepository personRepository;
  @Mock private SpringDataHomeJpaRepository homeRepository;
  @Mock private SpringDataRoleJpaRepository roleRepository;

  private MemberHomePersistenceAdapter adapter;
  private final MemberHomePersistenceMapper mapper = new MemberHomePersistenceMapper();

  @BeforeEach
  void setUp() {
    adapter = new MemberHomePersistenceAdapter(
        repository, personRepository, mapper, homeRepository, roleRepository);
  }

  private PersonJpaEntity persona(UUID id) {
    return new PersonJpaEntity(
        id, "Carlos", "Ruiz", "carlos@familia.com",
        "hash", LocalDateTime.now(), true);
  }

  private HomeJpaEntity hogar(UUID id) {
    return new HomeJpaEntity(id, "Los García", LocalDateTime.now());
  }

  // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

  @Test
  void guardarMemberHomePersisteEntidad() {
    // Arrange
    UUID homeId = UUID.randomUUID();
    UUID personId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    when(repository.save(any(MemberHomeJpaEntity.class)))
        .thenReturn(new MemberHomeJpaEntity(new MemberHomeJpaEntityId(homeId, personId), roleId));

    // Act
    adapter.saveMemberHome(homeId, personId, roleId);

    // Assert
    verify(repository, times(1)).save(any(MemberHomeJpaEntity.class));
  }

  @Test
  void eliminarMemberHomeInvocaDeleteByIds() {
    // Arrange
    UUID homeId = UUID.randomUUID();
    UUID personId = UUID.randomUUID();

    // Act
    adapter.deleteMemberHome(homeId, personId);

    // Assert
    verify(repository, times(1)).deleteByIdHomeIdAndIdPersonId(homeId, personId);
  }

  @Test
  void obtenerMemberHomeExistenteRetornaDtoCompleto() {
    // Arrange
    UUID homeId = UUID.randomUUID();
    UUID personId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    MemberHomeJpaEntity mh = new MemberHomeJpaEntity(
        new MemberHomeJpaEntityId(homeId, personId), roleId);
    when(repository.findByIdPersonIdAndIdHomeId(personId, homeId))
        .thenReturn(Optional.of(mh));
    when(personRepository.findById(personId)).thenReturn(Optional.of(persona(personId)));
    when(homeRepository.findById(homeId)).thenReturn(Optional.of(hogar(homeId)));

    // Act
    Optional<MemberHomeDto> dto = adapter.getMemberHome(personId, homeId);

    // Assert
    assertTrue(dto.isPresent());
    assertEquals(homeId.toString(), dto.get().homeId());
    assertEquals(personId.toString(), dto.get().personId());
    assertEquals("Carlos", dto.get().name());
    assertEquals("Los García", dto.get().homeName());
  }

  @Test
  void obtenerTodosLosMiembrosRetornaListaMapeada() {
    // Arrange
    UUID homeId = UUID.randomUUID();
    UUID personId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    MemberHomeJpaEntity mh = new MemberHomeJpaEntity(
        new MemberHomeJpaEntityId(homeId, personId), roleId);
    when(repository.findAllByIdHomeId(homeId)).thenReturn(List.of(mh));
    when(personRepository.findById(personId)).thenReturn(Optional.of(persona(personId)));

    // Act
    List<MemberDto> miembros = adapter.getAllMemberHome(homeId);

    // Assert
    assertEquals(1, miembros.size());
    assertEquals("Carlos", miembros.get(0).namePerson());
    assertEquals(roleId, miembros.get(0).roleId());
  }

  @Test
  void actualizarRolDeMiembroExistenteGuardaCambio() {
    // Arrange
    UUID homeId = UUID.randomUUID();
    UUID personId = UUID.randomUUID();
    UUID nuevoRol = UUID.randomUUID();
    MemberHomeJpaEntity mh = new MemberHomeJpaEntity(
        new MemberHomeJpaEntityId(homeId, personId), UUID.randomUUID());
    when(repository.findById(new MemberHomeJpaEntityId(homeId, personId)))
        .thenReturn(Optional.of(mh));

    // Act
    adapter.updateRoleMemberHome(homeId, personId, nuevoRol);

    // Assert
    assertEquals(nuevoRol, mh.getRoleId());
    verify(repository, times(1)).save(mh);
  }

  @Test
  void obtenerNombresHogaresPorPersonaRetornaListaMapeada() {
    // Arrange
    UUID personId = UUID.randomUUID();
    UUID homeId = UUID.randomUUID();
    when(repository.findAllHomeIdsByIdPersonId(personId)).thenReturn(List.of(homeId));
    when(homeRepository.findById(homeId)).thenReturn(Optional.of(hogar(homeId)));

    // Act
    List<String> nombres = adapter.getAllHomesByPersonId(personId);

    // Assert
    assertEquals(1, nombres.size());
    assertEquals("Los García", nombres.get(0));
  }

  @Test
  void obtenerNombresRolesPorPersonaRetornaListaMapeada() {
    // Arrange
    UUID personId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    when(repository.findAllRoleIdsByIdPersonId(personId)).thenReturn(List.of(roleId));
    when(roleRepository.findById(roleId))
        .thenReturn(Optional.of(new RoleJpaEntity(roleId, "ADMIN")));

    // Act
    List<String> roles = adapter.getAllRolesById(personId);

    // Assert
    assertEquals(1, roles.size());
    assertEquals("ADMIN", roles.get(0));
  }

  // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

  @Test
  void guardarMemberHomeFallaSiRepositorioRetornaNull() {
    // Arrange
    UUID homeId = UUID.randomUUID();
    UUID personId = UUID.randomUUID();
    UUID roleId = UUID.randomUUID();
    when(repository.save(any(MemberHomeJpaEntity.class))).thenReturn(null);

    // Act - Assert
    assertThrows(IllegalStateException.class,
        () -> adapter.saveMemberHome(homeId, personId, roleId));
  }

  @Test
  void obtenerMemberHomeInexistenteRetornaEmpty() {
    // Arrange
    UUID homeId = UUID.randomUUID();
    UUID personId = UUID.randomUUID();
    when(repository.findByIdPersonIdAndIdHomeId(personId, homeId))
        .thenReturn(Optional.empty());

    // Act
    Optional<MemberHomeDto> dto = adapter.getMemberHome(personId, homeId);

    // Assert
    assertFalse(dto.isPresent());
  }

  @Test
  void actualizarRolDeMiembroInexistenteLanzaExcepcion() {
    // Arrange
    UUID homeId = UUID.randomUUID();
    UUID personId = UUID.randomUUID();
    when(repository.findById(new MemberHomeJpaEntityId(homeId, personId)))
        .thenReturn(Optional.empty());

    // Act - Assert
    assertThrows(RuntimeException.class,
        () -> adapter.updateRoleMemberHome(homeId, personId, UUID.randomUUID()));
  }
}
