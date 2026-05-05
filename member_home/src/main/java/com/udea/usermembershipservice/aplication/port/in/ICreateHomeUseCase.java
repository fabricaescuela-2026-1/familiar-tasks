package com.udea.usermembershipservice.aplication.port.in;

import java.util.List;

import com.udea.usermembershipservice.aplication.useCase.dto.home.CreateHomeDto;
import com.udea.usermembershipservice.aplication.useCase.dto.home.HomeDto;

public interface ICreateHomeUseCase {

    void createdHome(CreateHomeDto createHomeDto);

    List<HomeDto> geatAllHomes();

    HomeDto getHomeByName(String name);

    void deleteHome(String name);
}
