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
            throw new IllegalStateException("Error saving person");
        }
    }

    @Override
    public List<Person> getAllUsers() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Person> getUserByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Person> getUserById(UUID idPerson) {
        return repository.findById(idPerson).map(mapper::toDomain);
    }

    @Override
    public void deleteUser(String email) {
        var person = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        repository.deleteById(person.getId());
    }

}
