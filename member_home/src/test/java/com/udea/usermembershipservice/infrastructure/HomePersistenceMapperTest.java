package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.domain.model.Home;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.HomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.HomePersistenceMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HomePersistenceMapperTest {

    private final HomePersistenceMapper mapper = new HomePersistenceMapper();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toDomainConviertEntidadADominio() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        HomeJpaEntity entity = new HomeJpaEntity(id, "Los García", ahora);

        // Act
        Home home = mapper.toDomain(entity);

        // Assert
        assertEquals(id, home.getIdHome());
        assertEquals("Los García", home.getName());
        assertEquals(ahora, home.getCreatedAt());
    }

    @Test
    void toEntityConviertesDominioAEntidad() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        Home home = Home.create(id, "Los García", ahora);

        // Act
        HomeJpaEntity entity = mapper.toEntity(home);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals("Los García", entity.getName());
        assertEquals(ahora, entity.getCreatedAt());
    }

    @Test
    void conversionIdaYVueltaEsConsistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        Home original = Home.create(id, "Los García", ahora);

        // Act
        Home resultado = mapper.toDomain(mapper.toEntity(original));

        // Assert
        assertEquals(original.getIdHome(), resultado.getIdHome());
        assertEquals(original.getName(), resultado.getName());
    }
}
