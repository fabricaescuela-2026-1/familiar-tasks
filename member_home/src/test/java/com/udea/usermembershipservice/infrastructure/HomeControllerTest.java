package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.aplication.port.in.ICreateHomeUseCase;
import com.udea.usermembershipservice.aplication.port.in.ICreatedMemberHome;
import com.udea.usermembershipservice.aplication.useCase.dto.home.CreateHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.home.HomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberDto;
import com.udea.usermembershipservice.infrastructure.adapter.in.web.HomeController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock private ICreateHomeUseCase createHomeUseCase;
    @Mock private ICreatedMemberHome createdMemberHome;

    @InjectMocks
    private HomeController controller;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void registerHomeRetorna200YDelegaAlUseCase() {
        // Arrange
        CreateHomeDto dto = new CreateHomeDto("Hogar1", "u@mail.com");

        // Act
        ResponseEntity<Void> response = controller.registerHome(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(createHomeUseCase).createdHome(dto);
    }

    @Test
    void getAllHomesRetornaListaDeHogares() {
        // Arrange
        HomeDto home = new HomeDto(UUID.randomUUID(), "Hogar1", LocalDateTime.of(2026, 1, 1, 10, 0, 0));
        when(createHomeUseCase.geatAllHomes()).thenReturn(List.of(home));

        // Act
        ResponseEntity<List<HomeDto>> response = controller.getAllHomes();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Hogar1", response.getBody().get(0).name());
    }

    @Test
    void getHomeByNameRetornaHogarBuscado() {
        // Arrange
        HomeDto home = new HomeDto(UUID.randomUUID(), "Hogar1", LocalDateTime.of(2026, 1, 1, 10, 0, 0));
        when(createHomeUseCase.getHomeByName("Hogar1")).thenReturn(home);

        // Act
        ResponseEntity<HomeDto> response = controller.getHomeByName("Hogar1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hogar1", response.getBody().name());
    }

    @Test
    void deleteHomeRetorna200YDelegaAlUseCase() {
        // Arrange - Act
        ResponseEntity<Void> response = controller.deleteHome("Hogar1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(createHomeUseCase).deleteHome("Hogar1");
    }

    @Test
    void getMemberHomeRetornaMiembrosDelHogar() {
        // Arrange
        MemberDto miembro = new MemberDto(UUID.randomUUID(), "Ana", "Lopez", "a@mail.com", UUID.randomUUID());
        when(createdMemberHome.getAllMemberHome("Hogar1")).thenReturn(List.of(miembro));

        // Act
        ResponseEntity<List<MemberDto>> response = controller.getMemberHome("Hogar1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Ana", response.getBody().get(0).namePerson());
    }
}
