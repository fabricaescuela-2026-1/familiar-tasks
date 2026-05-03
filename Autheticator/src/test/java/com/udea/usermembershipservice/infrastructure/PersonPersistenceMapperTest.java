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

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toDomainConvierteEntidadADominio() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        PersonJpaEntity entity = new PersonJpaEntity(id, "Ana", "López", "ana@mail.com", "Segura@123", ahora, true);

        // Act
        Person person = mapper.toDomain(entity);

        // Assert
        assertEquals(id, person.getIdPerson());
        assertEquals("Ana", person.getName());
        assertEquals("López", person.getLastName());
        assertEquals("ana@mail.com", person.getEmail());
        assertEquals("Segura@123", person.getPassword());
        assertTrue(person.getActive());
    }

    @Test
    void toEntityConvierteDominioAEntidad() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        Person person = Person.restore(id, "Ana", "López", "ana@mail.com", "Segura@123", ahora, true);

        // Act
        PersonJpaEntity entity = mapper.toEntity(person);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals("Ana", entity.getName());
        assertEquals("López", entity.getLastName());
        assertEquals("ana@mail.com", entity.getEmail());
        assertTrue(entity.isActive());
    }

    @Test
    void conversionIdaYVueltaEsConsistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        Person original = Person.restore(id, "Ana", "López", "ana@mail.com", "Segura@123", LocalDateTime.now(), true);

        // Act
        Person resultado = mapper.toDomain(mapper.toEntity(original));

        // Assert
        assertEquals(original.getIdPerson(), resultado.getIdPerson());
        assertEquals(original.getEmail(), resultado.getEmail());
        assertEquals(original.getName(), resultado.getName());
    }
}
