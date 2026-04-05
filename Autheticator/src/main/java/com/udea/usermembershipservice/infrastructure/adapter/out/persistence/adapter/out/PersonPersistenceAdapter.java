package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.domain.model.Person;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.PersonPersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataJpaRepository;

public class PersonPersistenceAdapter implements IPersonRepositoryPort {

    private final SpringDataJpaRepository repository;
    private final PersonPersistenceMapper mapper;

    public PersonPersistenceAdapter(SpringDataJpaRepository personJpaRepository, PersonPersistenceMapper personPersistenceMapper) {
        this.repository = personJpaRepository;
        this.mapper = personPersistenceMapper;
    }

    @Override
    public void saveUser(Person person) {
        var save = repository.save(mapper.toEntity(person));
        if (save == null) {
            throw new RuntimeException("Error saving person");
        }
    }

    @Override
    public List<Person> getAllUsers() {
        List<Person> persons = repository.findAll().stream().map(mapper::toDomain).toList();
        if (persons == null) {
            throw new RuntimeException("Error getting all users");
        }
        return persons;
    }

    @Override
    public Optional<Person> getUserByEmail(String email) {
        Optional<Person> person = repository.findByEmail(email).map(mapper::toDomain);
        if (person.isEmpty()) {
            return Optional.empty();
        }
        return person;
    }

    @Override
    public Optional<Person> getUserById(UUID idPerson) {
        Optional<Person> person = repository.findById(idPerson).map(mapper::toDomain);
        if (person.isEmpty()) {
            throw new RuntimeException("Error getting user by id");
        }
        return person;
    }



    @Override
    public void deleteUser(String email) {
        try {
            var person = repository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            repository.deleteById(person.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

}
