package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.CreateMemberHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.domain.model.Home;
import com.udea.usermembershipservice.domain.model.Person;
import com.udea.usermembershipservice.domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMemberHomeUseCaseTest {

    @Mock private IHomeRepositoryPort homeRepositoryPort;
    @Mock private IPersonRepositoryPort personRepositoryPort;
    @Mock private IRoleRepositoryPort roleRepositoryPort;
    @Mock private IMemberHomeRepositoryPort memberHomeRepositoryPort;

    @InjectMocks
    private CreateMemberHomeUseCase useCase;

    private UUID homeId;
    private UUID personId;
    private UUID adminId;
    private UUID adminRoleId;
    private UUID newRoleId;

    private Home home;
    private Person person;
    private Person admin;
    private Role adminRole;
    private Role newRole;
    private MemberHomeDto adminMembership;

    @BeforeEach
    void setUp() {
        homeId      = UUID.randomUUID();
        personId    = UUID.randomUUID();
        adminId     = UUID.randomUUID();
        adminRoleId = UUID.randomUUID();
        newRoleId   = UUID.randomUUID();

        home   = Home.create(homeId, "Los García", LocalDateTime.now());
        person = Person.restore(personId, "Carlos", "Díaz", "carlos@mail.com", "pass123", LocalDateTime.now(), true);
        admin  = Person.restore(adminId,  "Ana",    "López", "ana@mail.com",    "pass123", LocalDateTime.now(), true);

        adminRole = Role.create(adminRoleId, "Administrador");
        newRole   = Role.create(newRoleId,   "Miembro");

        adminMembership = new MemberHomeDto(
            homeId.toString(), adminId.toString(),
            "Ana", "López", "Los García", "ana@mail.com",
            adminRoleId, true
        );
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void administradorActualizaRolDeOtroMiembro() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(person));
        when(roleRepositoryPort.getRoleByName("Miembro")).thenReturn(Optional.of(newRole));
        when(personRepositoryPort.getUserByEmail("ana@mail.com")).thenReturn(Optional.of(admin));
        when(memberHomeRepositoryPort.getMemberHome(adminId, homeId)).thenReturn(Optional.of(adminMembership));
        when(roleRepositoryPort.getRoleByName("Administrador")).thenReturn(Optional.of(adminRole));

        // Act
        assertDoesNotThrow(() ->
            useCase.updateRoleMemberHome("Los García", "carlos@mail.com", "Miembro", "ana@mail.com")
        );

        // Assert
        verify(memberHomeRepositoryPort).updateRoleMemberHome(homeId, personId, newRoleId);
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void quienActualizaNoEsAdminLanzaExcepcion() {
        // Arrange
        UUID otroRolId = UUID.randomUUID();
        MemberHomeDto membresiaNoAdmin = new MemberHomeDto(
            homeId.toString(), adminId.toString(),
            "Ana", "López", "Los García", "ana@mail.com",
            otroRolId, true
        );

        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(person));
        when(roleRepositoryPort.getRoleByName("Miembro")).thenReturn(Optional.of(newRole));
        when(personRepositoryPort.getUserByEmail("ana@mail.com")).thenReturn(Optional.of(admin));
        when(memberHomeRepositoryPort.getMemberHome(adminId, homeId)).thenReturn(Optional.of(membresiaNoAdmin));
        when(roleRepositoryPort.getRoleByName("Administrador")).thenReturn(Optional.of(adminRole));

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.updateRoleMemberHome("Los García", "carlos@mail.com", "Miembro", "ana@mail.com")
        );
    }

    @Test
    void supuestoAdminNoEsMiembroDelHogarLanzaExcepcion() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(person));
        when(roleRepositoryPort.getRoleByName("Miembro")).thenReturn(Optional.of(newRole));
        when(personRepositoryPort.getUserByEmail("ana@mail.com")).thenReturn(Optional.of(admin));
        when(memberHomeRepositoryPort.getMemberHome(adminId, homeId)).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.updateRoleMemberHome("Los García", "carlos@mail.com", "Miembro", "ana@mail.com")
        );
    }

    @Test
    void hogarNoEncontradoLanzaExcepcion() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Inexistente")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.updateRoleMemberHome("Inexistente", "carlos@mail.com", "Miembro", "ana@mail.com")
        );
    }

    @Test
    void miembroAActualizarNoExisteLanzaExcepcion() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));
        when(personRepositoryPort.getUserByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.updateRoleMemberHome("Los García", "noexiste@mail.com", "Miembro", "ana@mail.com")
        );
    }
}
