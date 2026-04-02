package com.udea.usermembershipservice.aplication.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.udea.usermembershipservice.domain.model.Person;

public interface IPersonRepositoryPort {
    public void saveUser(Person person);
    public List<Person> getAllUsers();
    public Optional<Person> getUserByEmail(String email);
    public Optional<Person> getUserById(UUID idPerson);
    public void deleteUser(String email);
    
}
