package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.out.IAuditLogQueuePort;
import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.CreateMemberHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
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
import java.util.List;
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
    @Mock private IAuditLogQueuePort auditLogQueuePort;

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
        verify(auditLogQueuePort).publishRoleChanged(adminId, "carlos@mail.com");
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

    // HU11 Scenario 3 — el único administrador no puede quitarse su propio rol
    @Test
    void unicoAdminNoPuedeQuitarseElRol() {
        // Arrange
        MemberHomeDto membresiaAdmin = new MemberHomeDto(
            homeId.toString(), adminId.toString(),
            "Ana", "López", "Los García", "ana@mail.com",
            adminRoleId, true
        );

        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));
        when(personRepositoryPort.getUserByEmail("ana@mail.com")).thenReturn(Optional.of(admin));
        when(roleRepositoryPort.getRoleByName("Miembro")).thenReturn(Optional.of(newRole));
        when(memberHomeRepositoryPort.getMemberHome(adminId, homeId)).thenReturn(Optional.of(membresiaAdmin));
        when(roleRepositoryPort.getRoleByName("Administrador")).thenReturn(Optional.of(adminRole));
        when(memberHomeRepositoryPort.getAllMemberHome(homeId))
            .thenReturn(List.of(new MemberDto(adminId, "Ana", "López", "ana@mail.com", adminRoleId)));

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.updateRoleMemberHome("Los García", "ana@mail.com", "Miembro", "ana@mail.com")
        );
    }

    // HU11 — agregar miembro al hogar exitosamente
    @Test
    void agregarMiembroAlHogarExitosamente() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(person));
        when(roleRepositoryPort.getRoleByName("Miembro")).thenReturn(Optional.of(newRole));

        // Act - Assert
        assertDoesNotThrow(() ->
            useCase.createdMemberHome("carlos@mail.com", "Miembro", "Los García")
        );
        verify(memberHomeRepositoryPort).saveMemberHome(homeId, personId, newRoleId);
    }

    @Test
    void agregarMiembroHogarInexistenteLanzaExcepcion() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Inexistente")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.createdMemberHome("carlos@mail.com", "Miembro", "Inexistente")
        );
    }

    @Test
    void eliminarMiembroDelHogarExitosamente() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(person));

        // Act - Assert
        assertDoesNotThrow(() ->
            useCase.deleteMemberHome("Los García", "carlos@mail.com")
        );
        verify(memberHomeRepositoryPort).deleteMemberHome(homeId, personId);
    }

    @Test
    void obtenerTodosLosMiembrosDelHogar() {
        // Arrange
        MemberDto miembro = new MemberDto(personId, "Carlos", "Díaz", "carlos@mail.com", newRoleId);
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));
        when(memberHomeRepositoryPort.getAllMemberHome(homeId)).thenReturn(List.of(miembro));

        // Act
        List<MemberDto> result = useCase.getAllMemberHome("Los García");

        // Assert
        assertEquals(1, result.size());
        assertEquals("carlos@mail.com", result.get(0).gmail());
    }

    @Test
    void obtenerMiembrosPorHogarInexistenteLanzaExcepcion() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Inexistente")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(SearchException.class, () ->
            useCase.getAllMemberHome("Inexistente")
        );
    }
}
