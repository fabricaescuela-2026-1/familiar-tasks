package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.useCase.dto.audit.RoleChangedLog;
import com.udea.usermembershipservice.aplication.useCase.dto.auth.AuthRefreshResponse;
import com.udea.usermembershipservice.aplication.useCase.dto.home.CreateHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.home.HomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.CreatedMemberHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.queue.UserRegistrationEvent;
import com.udea.usermembershipservice.aplication.useCase.dto.role.CreateRoleDto;
import com.udea.usermembershipservice.aplication.useCase.dto.role.RoleDto;
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
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0, 0);

        // Act
        ApiErrorResponseDto dto = new ApiErrorResponseDto(now, 400, "Bad Request", "campo requerido");

        // Assert
        assertEquals(now, dto.timestamp());
        assertEquals(400, dto.status());
        assertEquals("Bad Request", dto.error());
        assertEquals("campo requerido", dto.message());
    }

    @Test
    void createHomeDtoAlmacenaCamposCorrectamente() {
        // Arrange - Act
        CreateHomeDto dto = new CreateHomeDto("Casa", "u@mail.com");

        // Assert
        assertEquals("Casa", dto.name());
        assertEquals("u@mail.com", dto.gmail());
    }

    @Test
    void homeDtoAlmacenaCamposCorrectamente() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0, 0);

        // Act
        HomeDto dto = new HomeDto(id, "Casa", now);

        // Assert
        assertEquals(id, dto.idHome());
        assertEquals("Casa", dto.name());
        assertEquals(now, dto.createdAt());
    }

    @Test
    void createRoleDtoAlmacenaNombre() {
        // Arrange - Act
        CreateRoleDto dto = new CreateRoleDto("ADMIN");

        // Assert
        assertEquals("ADMIN", dto.name());
    }

    @Test
    void roleDtoAlmacenaCamposCorrectamente() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        RoleDto dto = new RoleDto(id, "MEMBER");

        // Assert
        assertEquals(id, dto.idRole());
        assertEquals("MEMBER", dto.name());
    }

    @Test
    void memberDtoAlmacenaCamposCorrectamente() {
        // Arrange
        UUID personId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        // Act
        MemberDto dto = new MemberDto(personId, "Ana", "Lopez", "a@mail.com", roleId);

        // Assert
        assertEquals(personId, dto.idPerson());
        assertEquals("Ana", dto.namePerson());
        assertEquals("Lopez", dto.lastNamePerson());
        assertEquals("a@mail.com", dto.gmail());
        assertEquals(roleId, dto.roleId());
    }

    @Test
    void memberHomeDtoAlmacenaCamposCorrectamente() {
        // Arrange
        UUID roleId = UUID.randomUUID();

        // Act
        MemberHomeDto dto = new MemberHomeDto("home1", "person1", "Ana", "Lopez",
            "Casa", "a@mail.com", roleId, true);

        // Assert
        assertEquals("home1", dto.homeId());
        assertEquals("person1", dto.personId());
        assertEquals("Ana", dto.name());
        assertEquals("Lopez", dto.last_name());
        assertEquals("Casa", dto.homeName());
        assertEquals("a@mail.com", dto.email());
        assertEquals(roleId, dto.roleId());
        assertTrue(dto.active());
    }

    @Test
    void userRegistrationEventAlmacenaCamposCorrectamente() {
        // Arrange - Act
        UserRegistrationEvent event = new UserRegistrationEvent(
            "uid", "Pepe", "Gomez", "p@mail.com", "hash", "2026-01-01T00:00:00");

        // Assert
        assertEquals("uid", event.userId());
        assertEquals("Pepe", event.name());
        assertEquals("Gomez", event.lastname());
        assertEquals("p@mail.com", event.email());
        assertEquals("hash", event.passwordHash());
        assertEquals("2026-01-01T00:00:00", event.createdAt());
    }

    @Test
    void authRefreshResponseAsignaTokens() {
        // Arrange - Act
        AuthRefreshResponse response = AuthRefreshResponse.builder()
            .accessToken("access")
            .refreshToken("refresh")
            .build();

        // Assert
        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }
}
