package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.in.ILoginUserCase;
import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.CreatedHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.home.CreateHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginResultDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.domain.model.Home;
import com.udea.usermembershipservice.domain.model.Person;
import com.udea.usermembershipservice.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatedHomeUseCaseTest {

    @Mock private IHomeRepositoryPort homeRepositoryPort;
    @Mock private ILoginUserCase loginUserCase;
    @Mock private IPersonRepositoryPort personRepositoryPort;
    @Mock private IRoleRepositoryPort roleRepositoryPort;
    @Mock private IMemberHomeRepositoryPort memberHomeRepositoryPort;

    @InjectMocks
    private CreatedHomeUseCase useCase;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    // HU08 Scenario 1
    @Test
    void creacionDeGrupoExitosa() {
        // Arrange
        var creator = Person.restore(UUID.randomUUID(), "Carlos", "Ruiz", "carlos@mail.com", "Segura@123", LocalDateTime.now(), true);
        var adminRole = Role.create(UUID.randomUUID(), "Administrador");
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.empty());
        when(loginUserCase.login(any())).thenReturn(new LoginResultDto(true, "ok"));
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(creator));
        when(roleRepositoryPort.getRoleByName("Administrador")).thenReturn(Optional.of(adminRole));

        // Act - Assert
        assertDoesNotThrow(() ->
            useCase.createdHome(new CreateHomeDto("Los García", "carlos@mail.com", "Segura@123"))
        );
        verify(homeRepositoryPort).saveHome(any(Home.class));
        verify(memberHomeRepositoryPort).saveMemberHome(any(), any(), any());
    }

    // ── CASOS DE EXCEPCIÓN ──────────────────────────────────────────────────

    // HU08 Scenario 2
    @Test
    void nombreVacioNoCrearElGrupo() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("")).thenReturn(Optional.empty());
        when(loginUserCase.login(any())).thenReturn(new LoginResultDto(true, "ok"));

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.createdHome(new CreateHomeDto("", "carlos@mail.com", "Segura@123"))
        );
        verify(homeRepositoryPort, never()).saveHome(any());
    }
}
