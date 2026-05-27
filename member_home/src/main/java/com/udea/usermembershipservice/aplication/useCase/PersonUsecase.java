package com.udea.usermembershipservice.aplication.useCase;

import java.util.List;

import com.udea.usermembershipservice.aplication.port.in.IPersonUseCase;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.dto.person.PersonDTO;
import com.udea.usermembershipservice.aplication.useCase.exception.SearchException;
import com.udea.usermembershipservice.domain.model.Person;

public class PersonUsecase implements IPersonUseCase {

    private final IPersonRepositoryPort personRepositoryPort;
    private final IMemberHomeRepositoryPort memberHomeRepositoryPort;
    private final IRoleRepositoryPort roleRepositoryPort;

    public PersonUsecase(IPersonRepositoryPort personRepositoryPort, IMemberHomeRepositoryPort memberHomeRepositoryPort, IRoleRepositoryPort roleRepositoryPort) {
        this.personRepositoryPort = personRepositoryPort;
        this.memberHomeRepositoryPort = memberHomeRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public List<PersonDTO> getAllPersons() {
        List<Person> persons = personRepositoryPort.getAllUsers();
        return persons.stream().map(person -> {
            List<String> homes = memberHomeRepositoryPort.getAllHomesByPersonId(person.getIdPerson());
            List<String> roles = memberHomeRepositoryPort.getAllRolesById(person.getIdPerson());
            return new PersonDTO(person.getIdPerson(), person.getName(), person.getEmail(), person.getActive(), homes, roles);
        }).toList();
    }

    @Override
    public PersonDTO getPersonByEmail(String email) {
        Person person = personRepositoryPort.getUserByEmail(email).orElseThrow(() -> new SearchException("Person not found"));
        List<String> homes = memberHomeRepositoryPort.getAllHomesByPersonId(person.getIdPerson());
        List<String> roles = memberHomeRepositoryPort.getAllRolesById(person.getIdPerson());
        return new PersonDTO(person.getIdPerson(), person.getName(), person.getEmail(), person.getActive(), homes, roles);
    }



}
