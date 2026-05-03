package com.udea.usermembershipservice.aplication;

import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.CreatedHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.dto.home.CreateHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.home.HomeDto;
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
    @Mock private IPersonRepositoryPort personRepositoryPort;
    @Mock private IRoleRepositoryPort roleRepositoryPort;
    @Mock private IMemberHomeRepositoryPort memberHomeRepositoryPort;

    @InjectMocks
    private CreatedHomeUseCase useCase;

    @Test
    void creacionDeGrupoExitosa() {
        var creator = Person.restore(UUID.randomUUID(), "Carlos", "Ruiz", "carlos@mail.com", "hashed", LocalDateTime.now(), true);
        var adminRole = Role.create(UUID.randomUUID(), "Administrador");
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.empty());
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(Optional.of(creator));
        when(roleRepositoryPort.getRoleByName("Administrador")).thenReturn(Optional.of(adminRole));

        assertDoesNotThrow(() ->
            useCase.createdHome(new CreateHomeDto("Los García", "carlos@mail.com"))
        );
        verify(homeRepositoryPort).saveHome(any(Home.class));
        verify(memberHomeRepositoryPort).saveMemberHome(any(), any(), any());
    }

    @Test
    void nombreVacioNoCrearElGrupo() {
        when(homeRepositoryPort.getHomeByName("")).thenReturn(Optional.empty());
        when(personRepositoryPort.getUserByEmail("carlos@mail.com")).thenReturn(
            Optional.of(Person.restore(UUID.randomUUID(), "Carlos", "Ruiz", "carlos@mail.com", "hashed", LocalDateTime.now(), true))
        );
        when(roleRepositoryPort.getRoleByName("Administrador")).thenReturn(
            Optional.of(Role.create(UUID.randomUUID(), "Administrador"))
        );

        assertThrows(PersistenceException.class, () ->
            useCase.createdHome(new CreateHomeDto("", "carlos@mail.com"))
        );
        verify(homeRepositoryPort, never()).saveHome(any());
    }

    @Test
    void hogarConNombreYaExistenteLanzaExcepcion() {
        var home = Home.create(UUID.randomUUID(), "Los García", LocalDateTime.now());
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));

        assertThrows(PersistenceException.class, () ->
            useCase.createdHome(new CreateHomeDto("Los García", "carlos@mail.com"))
        );
        verify(homeRepositoryPort, never()).saveHome(any());
    }

    @Test
    void obtenerTodosLosHogaresRetornaLista() {
        var home = Home.create(UUID.randomUUID(), "Los García", LocalDateTime.now());
        when(homeRepositoryPort.getAllHomes()).thenReturn(List.of(home));

        List<HomeDto> result = useCase.geatAllHomes();

        assertEquals(1, result.size());
        assertEquals("Los García", result.get(0).name());
    }

    @Test
    void obtenerHogarPorNombreExistenteRetornaDto() {
        var home = Home.create(UUID.randomUUID(), "Los García", LocalDateTime.now());
        when(homeRepositoryPort.getHomeByName("Los García")).thenReturn(Optional.of(home));

        HomeDto result = useCase.getHomeByName("Los García");

        assertEquals("Los García", result.name());
    }

    @Test
    void obtenerHogarPorNombreInexistenteLanzaExcepcion() {
        when(homeRepositoryPort.getHomeByName("Inexistente")).thenReturn(Optional.empty());

        assertThrows(SearchException.class, () ->
            useCase.getHomeByName("Inexistente")
        );
    }

    @Test
    void eliminarHogarExistenteEliminaCorrectamente() {
        doNothing().when(homeRepositoryPort).deleteHome("Los García");

        assertDoesNotThrow(() -> useCase.deleteHome("Los García"));
        verify(homeRepositoryPort).deleteHome("Los García");
    }
}
