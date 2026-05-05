package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.domain.model.Role;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.RolePersistenceMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RolePersistenceMapperTest {

    private final RolePersistenceMapper mapper = new RolePersistenceMapper();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toDomainConvierteEntidadADominio() {
        // Arrange
        UUID id = UUID.randomUUID();
        RoleJpaEntity entity = new RoleJpaEntity(id, "Administrador");

        // Act
        Role role = mapper.toDomain(entity);

        // Assert
        assertEquals(id, role.getIdRole());
        assertEquals("Administrador", role.getName());
    }

    @Test
    void toEntityConvierteDominioAEntidad() {
        // Arrange
        UUID id = UUID.randomUUID();
        Role role = Role.create(id, "Miembro");

        // Act
        RoleJpaEntity entity = mapper.toEntity(role);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals("Miembro", entity.getName());
    }

    @Test
    void conversionIdaYVueltaEsConsistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        Role original = Role.create(id, "Administrador");

        // Act
        Role resultado = mapper.toDomain(mapper.toEntity(original));

        // Assert
        assertEquals(original.getIdRole(), resultado.getIdRole());
        assertEquals(original.getName(), resultado.getName());
    }
}
