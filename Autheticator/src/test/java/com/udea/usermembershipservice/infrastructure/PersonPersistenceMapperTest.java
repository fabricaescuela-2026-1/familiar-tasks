package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.domain.model.Person;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.PersonJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.PersonPersistenceMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PersonPersistenceMapperTest {

    private final PersonPersistenceMapper mapper = new PersonPersistenceMapper();

    @Test
    void toDomainConvierteEntidadADominio() {
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        PersonJpaEntity entity = new PersonJpaEntity(id, "Ana", "López", "ana@mail.com", "hash-Segura@123", ahora, true);

        Person person = mapper.toDomain(entity);

        assertEquals(id, person.getIdPerson());
        assertEquals("Ana", person.getName());
        assertEquals("López", person.getLastName());
        assertEquals("ana@mail.com", person.getEmail());
        assertEquals("hash-Segura@123", person.getPasswordHash());
        assertTrue(person.getActive());
    }

    @Test
    void toEntityConvierteDominioAEntidad() {
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        Person person = Person.restore(id, "Ana", "López", "ana@mail.com", "hash-Segura@123", ahora, true);

        PersonJpaEntity entity = mapper.toEntity(person);

        assertEquals(id, entity.getId());
        assertEquals("Ana", entity.getName());
        assertEquals("López", entity.getLastName());
        assertEquals("ana@mail.com", entity.getEmail());
        assertEquals("hash-Segura@123", entity.getPassword());
        assertTrue(entity.isActive());
    }

    @Test
    void conversionIdaYVueltaEsConsistente() {
        UUID id = UUID.randomUUID();
        Person original = Person.restore(id, "Ana", "López", "ana@mail.com", "hash-Segura@123", LocalDateTime.now(), true);

        Person resultado = mapper.toDomain(mapper.toEntity(original));

        assertEquals(original.getIdPerson(), resultado.getIdPerson());
        assertEquals(original.getEmail(), resultado.getEmail());
        assertEquals(original.getName(), resultado.getName());
        assertEquals(original.getPasswordHash(), resultado.getPasswordHash());
    }
}
