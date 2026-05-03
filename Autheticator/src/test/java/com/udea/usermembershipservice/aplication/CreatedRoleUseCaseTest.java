package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.CreatedRoleUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.role.CreateRoleDto;
import com.udea.usermembershipservice.aplication.useCase.dto.role.RoleDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
import com.udea.usermembershipservice.domain.model.Person;
import com.udea.usermembershipservice.domain.model.Role;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatedRoleUseCaseTest {

    @Mock private IRoleRepositoryPort roleRepositoryPort;
    @Mock private IPersonRepositoryPort personRepositoryPort;

    @InjectMocks
    private CreatedRoleUseCase useCase;

    private Person personaActiva() {
        return Person.restore(
            UUID.randomUUID(), "Ana", "López",
            "ana@mail.com", "hashedPass", LocalDateTime.now(), true
        );
    }

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void crearRolNuevoExitosamente() {
        // Arrange
        when(roleRepositoryPort.getRoleByName("Miembro")).thenReturn(Optional.empty());
        when(personRepositoryPort.getUserByEmail("ana@mail.com")).thenReturn(Optional.of(personaActiva()));

        // Act - Assert
        assertDoesNotThrow(() ->
            useCase.createdRole(new CreateRoleDto("Miembro"), "ana@mail.com")
        );
        verify(roleRepositoryPort).saveRole(any(Role.class));
    }

    @Test
    void obtenerTodosLosRolesRetornaLista() {
        // Arrange
        Role adminRole = Role.create(UUID.randomUUID(), "Administrador");
        when(roleRepositoryPort.getAllRoles()).thenReturn(List.of(adminRole));

        // Act
        List<RoleDto> result = useCase.geatAllRoles();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Administrador", result.get(0).name());
    }

    @Test
    void obtenerRolPorNombreExistenteRetornaDto() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        Role role = Role.create(roleId, "Administrador");
        when(roleRepositoryPort.getRoleByName("Administrador")).thenReturn(Optional.of(role));

        // Act
        RoleDto result = useCase.getRoleByName("Administrador");

        // Assert
        assertEquals("Administrador", result.name());
        assertEquals(roleId, result.idRole());
    }

    @Test
    void eliminarRolExistenteEliminaCorrectamente() {
        // Arrange
        doNothing().when(roleRepositoryPort).deleteRole("Miembro");

        // Act - Assert
        assertDoesNotThrow(() -> useCase.deleteRole("Miembro"));
        verify(roleRepositoryPort).deleteRole("Miembro");
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    @Test
    void crearRolDuplicadoLanzaExcepcion() {
        // Arrange
        Role existingRole = Role.create(UUID.randomUUID(), "Administrador");
        when(roleRepositoryPort.getRoleByName("Administrador")).thenReturn(Optional.of(existingRole));

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.createdRole(new CreateRoleDto("Administrador"), "ana@mail.com")
        );
        verify(roleRepositoryPort, never()).saveRole(any());
    }

    @Test
    void crearRolConUsuarioNoRegistradoLanzaExcepcion() {
        // Arrange
        when(roleRepositoryPort.getRoleByName("Miembro")).thenReturn(Optional.empty());
        when(personRepositoryPort.getUserByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.createdRole(new CreateRoleDto("Miembro"), "noexiste@mail.com")
        );
        verify(roleRepositoryPort, never()).saveRole(any());
    }

    @Test
    void obtenerRolPorNombreInexistenteLanzaExcepcion() {
        // Arrange
        when(roleRepositoryPort.getRoleByName("Inexistente")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(SearchException.class, () ->
            useCase.getRoleByName("Inexistente")
        );
    }
}
