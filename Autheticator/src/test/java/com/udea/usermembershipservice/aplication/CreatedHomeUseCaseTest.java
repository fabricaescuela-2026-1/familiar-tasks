package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.in.ILoginUserCase;
import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.CreatedHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.home.CreateHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.home.HomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginResultDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
import com.udea.usermembershipservice.domain.model.Home;
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

    @Test
    void hogarConNombreYaExistenteLanzaExcepcion() {
        // Arrange
        var home = Home.create(UUID.randomUUID(), "Los García", LocalDateTime.now());
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));

        // Act - Assert
        assertThrows(PersistenceException.class, () ->
            useCase.createdHome(new CreateHomeDto("Los García", "carlos@mail.com", "Segura@123"))
        );
        verify(homeRepositoryPort, never()).saveHome(any());
    }

    @Test
    void obtenerTodosLosHogaresRetornaLista() {
        // Arrange
        var home = Home.create(UUID.randomUUID(), "Los García", LocalDateTime.now());
        when(homeRepositoryPort.getAllHomes()).thenReturn(List.of(home));

        // Act
        List<HomeDto> result = useCase.geatAllHomes();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Los García", result.get(0).name());
    }

    @Test
    void obtenerHogarPorNombreExistenteRetornaDto() {
        // Arrange
        var home = Home.create(UUID.randomUUID(), "Los García", LocalDateTime.now());
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));

        // Act
        HomeDto result = useCase.getHomeByName("Los García");

        // Assert
        assertEquals("Los García", result.name());
    }

    @Test
    void obtenerHogarPorNombreInexistenteLanzaExcepcion() {
        // Arrange
        when(homeRepositoryPort.getHomeByName("Inexistente")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(SearchException.class, () ->
            useCase.getHomeByName("Inexistente")
        );
    }

    @Test
    void eliminarHogarExistenteEliminaCorrectamente() {
        // Arrange
        doNothing().when(homeRepositoryPort).deleteHome("Los García");

        // Act - Assert
        assertDoesNotThrow(() -> useCase.deleteHome("Los García"));
        verify(homeRepositoryPort).deleteHome("Los García");
    }
}
