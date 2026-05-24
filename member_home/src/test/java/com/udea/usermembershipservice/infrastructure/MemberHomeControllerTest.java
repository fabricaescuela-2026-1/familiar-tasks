package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.aplication.port.in.ICreatedMemberHome;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.CreatedMemberHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;
import com.udea.usermembershipservice.infrastructure.adapter.in.web.MemberHomeController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberHomeControllerTest {

    @Mock private ICreatedMemberHome createdMemberHome;

    @InjectMocks
    private MemberHomeController controller;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void saveMemberHomeDelegaParametrosAlUseCase() {
        // Arrange
        CreatedMemberHomeDto dto = new CreatedMemberHomeDto("u@mail.com", "ADMIN", "Hogar1");

        // Act
        controller.saveMemberHome(dto);

        // Assert
        verify(createdMemberHome).createdMemberHome("u@mail.com", "ADMIN", "Hogar1");
    }

    @Test
    void deleteMemberHomeDelegaAlUseCase() {
        // Arrange - Act
        controller.deleteMemberHome("Hogar1", "u@mail.com");

        // Assert
        verify(createdMemberHome).deleteMemberHome("Hogar1", "u@mail.com");
    }

    @Test
    void getMemberHomeRetornaCompletableFutureConVinculacion() {
        // Arrange
        UUID personId = UUID.randomUUID();
        UUID homeId = UUID.randomUUID();
        MemberHomeDto dto = new MemberHomeDto(homeId.toString(), personId.toString(),
            "Ana", "Lopez", "Hogar1", "a@mail.com", UUID.randomUUID(), true);
        when(createdMemberHome.getMemberHome(personId, homeId))
            .thenReturn(CompletableFuture.completedFuture(dto));

        // Act
        CompletableFuture<MemberHomeDto> resultado = controller.getMemberHome(personId, homeId);

        // Assert
        assertEquals("Ana", resultado.join().name());
        verify(createdMemberHome).getMemberHome(personId, homeId);
    }

    @Test
    void updateRoleMemberHomeRetorna200YMensaje() {
        // Arrange - Act
        ResponseEntity<String> response = controller.updateRoleMemberHome(
            "Hogar1", "u@mail.com", "OWNER", "admin@mail.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rol actualizado correctamente", response.getBody());
        verify(createdMemberHome).updateRoleMemberHome("Hogar1", "u@mail.com", "OWNER", "admin@mail.com");
    }
}
