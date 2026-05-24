package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.aplication.port.in.ICreateRoleUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.role.CreateRoleDto;
import com.udea.usermembershipservice.aplication.useCase.dto.role.RoleDto;
import com.udea.usermembershipservice.infrastructure.adapter.in.web.RoleController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock private ICreateRoleUseCase createRoleUseCase;

    @InjectMocks
    private RoleController controller;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void registerRoleRetorna200YDelegaAlUseCase() {
        // Arrange
        CreateRoleDto dto = new CreateRoleDto("ADMIN");

        // Act
        ResponseEntity<Void> response = controller.registerRole(dto, "admin@mail.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(createRoleUseCase).createdRole(dto, "admin@mail.com");
    }

    @Test
    void getAllRolesRetornaListaDeRoles() {
        // Arrange
        RoleDto role = new RoleDto(UUID.randomUUID(), "ADMIN");
        when(createRoleUseCase.geatAllRoles()).thenReturn(List.of(role));

        // Act
        ResponseEntity<List<RoleDto>> response = controller.getAllRoles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("ADMIN", response.getBody().get(0).name());
    }

    @Test
    void getRoleByNameRetornaRolBuscado() {
        // Arrange
        RoleDto role = new RoleDto(UUID.randomUUID(), "ADMIN");
        when(createRoleUseCase.getRoleByName("ADMIN")).thenReturn(role);

        // Act
        ResponseEntity<RoleDto> response = controller.getRoleByName("ADMIN");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ADMIN", response.getBody().name());
    }

    @Test
    void deleteRoleRetorna200YDelegaAlUseCase() {
        // Arrange - Act
        ResponseEntity<Void> response = controller.deleteRole("ADMIN");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(createRoleUseCase).deleteRole("ADMIN");
    }
}
