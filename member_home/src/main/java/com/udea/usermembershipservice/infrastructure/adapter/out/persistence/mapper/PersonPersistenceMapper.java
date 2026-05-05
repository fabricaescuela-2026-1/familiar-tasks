package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper;

import com.udea.usermembershipservice.domain.model.Person;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.PersonJpaEntity;

public class PersonPersistenceMapper {

    public PersonJpaEntity toEntity(Person person) {
        return new PersonJpaEntity(
            person.getIdPerson(),
            person.getName(),
            person.getLastName(),
            person.getEmail(),
            person.getPasswordHash(),
            person.getcreatedAt(),
            person.getActive()
        );
    }

    public Person toDomain(PersonJpaEntity personJpaEntity) {
        return Person.restore(
            personJpaEntity.getId(),
            personJpaEntity.getName(),
            personJpaEntity.getLastName(),
            personJpaEntity.getEmail(),
            personJpaEntity.getPassword(),
            personJpaEntity.getCreatedAt(),
            personJpaEntity.isActive()
        );
    }
}
