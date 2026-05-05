package com.udea.usermembershipservice.aplication.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.udea.usermembershipservice.domain.model.Home;

public interface IHomeRepositoryPort {

    void saveHome(Home home);

    List<Home> getAllHomes();

    Optional<Home> getHomeByName(String name);

    Optional<Home> getHomeById(UUID idHome);


    void deleteHome(String name);
}
