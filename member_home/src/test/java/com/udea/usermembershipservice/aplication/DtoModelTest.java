package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.useCase.dto.audit.RoleChangedLog;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.CreatedMemberHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.role.UpdateRoleDto;
import com.udea.usermembershipservice.infrastructure.adapter.in.web.error.ApiErrorResponseDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoModelTest {

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void createdMemberHomeDtoAlmacenaCamposCorrectamente() {
        // Arrange - Act
        CreatedMemberHomeDto dto = new CreatedMemberHomeDto("user@test.com", "ADMIN", "Mi Hogar");

        // Assert
        assertEquals("user@test.com", dto.gmail());
        assertEquals("ADMIN", dto.rol());
        assertEquals("Mi Hogar", dto.nameHogar());
    }

    @Test
    void updateRoleDtoAlmacenaCamposCorrectamente() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        UpdateRoleDto dto = new UpdateRoleDto(id, "MEMBER");

        // Assert
        assertEquals(id, dto.idRole());
        assertEquals("MEMBER", dto.name());
    }

    @Test
    void roleChangedLogFactoryMetodoGeneraInstanciaValida() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        RoleChangedLog log = RoleChangedLog.roleChanged(userId, "ROLE");

        // Assert
        assertNotNull(log.id());
        assertEquals(userId, log.idUser());
        assertEquals("ROLE", log.modifiedElement());
        assertEquals("role_changed", log.action());
    }

    @Test
    void roleChangedLogConstructorDirecto() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // Act
        RoleChangedLog log = new RoleChangedLog(id, userId, "TASK", "UPDATED");

        // Assert
        assertEquals(id, log.id());
        assertEquals(userId, log.idUser());
        assertEquals("TASK", log.modifiedElement());
        assertEquals("UPDATED", log.action());
    }

    @Test
    void apiErrorResponseDtoAlmacenaCamposCorrectamente() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        ApiErrorResponseDto dto = new ApiErrorResponseDto(now, 400, "Bad Request", "campo requerido");

        // Assert
        assertEquals(now, dto.timestamp());
        assertEquals(400, dto.status());
        assertEquals("Bad Request", dto.error());
        assertEquals("campo requerido", dto.message());
    }
}
