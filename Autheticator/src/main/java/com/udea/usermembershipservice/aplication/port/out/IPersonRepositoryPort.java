package com.udea.usermembershipservice.aplication.port.out;

import java.util.Optional;
import java.util.UUID;

import com.udea.usermembershipservice.domain.model.Person;

public interface IPersonRepositoryPort {
    void saveUser(Person person);
    Optional<Person> getUserByEmail(String email);
    Optional<Person> getUserById(UUID idPerson);
}
