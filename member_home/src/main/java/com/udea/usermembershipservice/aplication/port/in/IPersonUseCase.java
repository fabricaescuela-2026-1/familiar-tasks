package com.udea.usermembershipservice.aplication.port.in;

import java.util.List;

import com.udea.usermembershipservice.aplication.useCase.dto.person.PersonDTO;

public interface IPersonUseCase {
    List<PersonDTO> getAllPersons();
    PersonDTO getPersonByEmail(String email);

}
