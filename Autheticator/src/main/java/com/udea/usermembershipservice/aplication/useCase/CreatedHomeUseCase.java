package com.udea.usermembershipservice.aplication.useCase;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import com.udea.usermembershipservice.aplication.port.in.ICreateHomeUseCase;
import com.udea.usermembershipservice.aplication.port.in.ILoginUserCase;
import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.dto.home.CreateHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.home.HomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.login.LoginDto;
import com.udea.usermembershipservice.aplication.useCase.exception.PersistenceException;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
import com.udea.usermembershipservice.domain.model.Home;

public class CreatedHomeUseCase implements ICreateHomeUseCase {

    private final IHomeRepositoryPort homeRepositoryPort;
    private final ILoginUserCase loginUserCase;
    private final IPersonRepositoryPort personRepositoryPort;
    private final IRoleRepositoryPort roleRepositoryPort;
    private final IMemberHomeRepositoryPort memberHomeRepositoryPort;

    public CreatedHomeUseCase(IHomeRepositoryPort homeRepositoryPort, ILoginUserCase loginUserCase,
            IPersonRepositoryPort personRepositoryPort, IRoleRepositoryPort roleRepositoryPort,
            IMemberHomeRepositoryPort memberHomeRepositoryPort) {
        this.homeRepositoryPort = homeRepositoryPort;
        this.loginUserCase = loginUserCase;
        this.personRepositoryPort = personRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.memberHomeRepositoryPort = memberHomeRepositoryPort;
    }

    @Override
    public void createdHome(CreateHomeDto createHomeDto) {
        try {
            if (homeRepositoryPort.getHomeByName(createHomeDto.name()).isPresent()) {
                throw new RuntimeException("Home with this name already exists");
            }

            LoginDto loginDto = new LoginDto(createHomeDto.gmail(), createHomeDto.password());
            
            if(loginUserCase.login(loginDto).acces() == true){
                Home home = Home.create(
                UUID.randomUUID(),
                createHomeDto.name(),
                LocalDateTime.now(ZoneId.of("America/Bogota"))
            );

            homeRepositoryPort.saveHome(home);

            var creator = personRepositoryPort.getUserByEmail(createHomeDto.gmail())
                    .orElseThrow(() -> new RuntimeException("Creator not found"));
            var adminRole = roleRepositoryPort.getRoleByName("Administrador")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            memberHomeRepositoryPort.saveMemberHome(home.getIdHome(), creator.getIdPerson(), adminRole.getIdRole());
            }else{
                throw new RuntimeException("Invalid login credentials");
            }

            
        } catch (Exception e) {
            throw new PersistenceException("Error saving home", e);
        }
    }

    @Override
    public List<HomeDto> geatAllHomes() {
        try {
            return homeRepositoryPort.getAllHomes().stream()
                .map(home -> new HomeDto(home.getIdHome(), home.getName(), home.getCreatedAt()))
                .toList();
        } catch (Exception e) {
            throw new SearchException("Error getting all homes", e);
        }
    }

    @Override
    public HomeDto getHomeByName(String name) {
        try {
            Home home = homeRepositoryPort.getHomeByName(name)
                .orElseThrow(() -> new RuntimeException("Home not found"));

            return new HomeDto(home.getIdHome(), home.getName(), home.getCreatedAt());
        } catch (Exception e) {
            throw new SearchException("Error getting home by name", e);
        }
    }


    @Override
    public void deleteHome(String name) {
        try {
            homeRepositoryPort.deleteHome(name);
        } catch (Exception e) {
            throw new PersistenceException("Error deleting home", e);
        }
    }
}
