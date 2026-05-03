package com.udea.usermembershipservice.infrastructure;

import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.HomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntityId;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.PersonJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.MemberHomePersistenceMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MemberHomePersistenceMapperTest {

    private final MemberHomePersistenceMapper mapper = new MemberHomePersistenceMapper();

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void toEntityCreaMemberHomeJpaEntityCorrectamente() {
        // Arrange
        UUID homeId   = UUID.randomUUID();
        UUID personId = UUID.randomUUID();
        UUID roleId   = UUID.randomUUID();

        // Act
        MemberHomeJpaEntity entity = mapper.toEntity(homeId, personId, roleId);

        // Assert
        assertEquals(homeId,   entity.getId().getHomeId());
        assertEquals(personId, entity.getId().getPersonId());
        assertEquals(roleId,   entity.getRoleId());
    }

    @Test
    void toDtoConvierteEntidadesADto() {
        // Arrange
        UUID homeId   = UUID.randomUUID();
        UUID personId = UUID.randomUUID();
        UUID roleId   = UUID.randomUUID();

        MemberHomeJpaEntity memberHomeEntity = new MemberHomeJpaEntity(
            new MemberHomeJpaEntityId(homeId, personId), roleId
        );
        PersonJpaEntity personEntity = new PersonJpaEntity(
            personId, "Ana", "López", "ana@mail.com", "Segura@123", LocalDateTime.now(), true
        );
        HomeJpaEntity homeEntity = new HomeJpaEntity(homeId, "Los García", LocalDateTime.now());

        // Act
        MemberHomeDto dto = mapper.toDto(memberHomeEntity, personEntity, homeEntity);

        // Assert
        assertEquals(homeId.toString(),   dto.homeId());
        assertEquals(personId.toString(), dto.personId());
        assertEquals("Ana",              dto.name());
        assertEquals("López",            dto.last_name());
        assertEquals("Los García",       dto.homeName());
        assertEquals("ana@mail.com",     dto.email());
        assertEquals(roleId,             dto.roleId());
        assertTrue(dto.active());
    }
}
