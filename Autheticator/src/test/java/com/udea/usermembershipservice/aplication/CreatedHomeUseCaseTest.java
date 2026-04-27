package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.in.ILoginUserCase;
import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.CreatedHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.home.CreateHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginResultDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.domain.model.Home;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatedHomeUseCaseTest {

    @Mock private IHomeRepositoryPort homeRepositoryPort;
    @Mock private ILoginUserCase loginUserCase;
    @Mock private IMemberHomeRepositoryPort memberHomeRepositoryPort;

    @InjectMocks
    private CreatedHomeUseCase useCase;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    // HU08 Scenario 1
    @Test
    void creacionDeGrupoExitosa() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.empty());
        when(loginUserCase.login(any())).thenReturn(new LoginResultDto(true, "ok"));

        // Act - Assert
        assertDoesNotThrow(() ->
            useCase.createdHome(new CreateHomeDto("Los García", "carlos@mail.com", "Segura@123"))
        );
        verify(homeRepositoryPort).saveHome(any(Home.class));
    }

    // HU08 Scenario 1 — el creador debe quedar registrado como Administrador del grupo
    @Test
    void creadorDelGrupoQuedaComoAdministrador() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.empty());
        when(loginUserCase.login(any())).thenReturn(new LoginResultDto(true, "ok"));

        // Act
        useCase.createdHome(new CreateHomeDto("Los García", "carlos@mail.com", "Segura@123"));

        // Assert
        verify(memberHomeRepositoryPort, times(1)).saveMemberHome(any(), any(), any());
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

    // HU08 Scenario 3 — usuario ya en un grupo no puede crear otro sin salir del actual
    @Test
    void usuarioYaEnGrupoNoPuedeCrearOtro() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Nuevo Grupo")).thenReturn(Optional.empty());
        when(loginUserCase.login(any())).thenReturn(new LoginResultDto(true, "ok"));

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.createdHome(new CreateHomeDto("Nuevo Grupo", "carletto@mail.com", "Segura@123"))
        );
    }
}
