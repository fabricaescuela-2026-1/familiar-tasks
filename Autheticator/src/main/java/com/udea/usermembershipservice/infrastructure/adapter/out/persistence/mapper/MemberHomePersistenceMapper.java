package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper;

import java.util.UUID;

import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;
import com.udea.usermembershipservice.domain.model.Home;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.HomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntityId;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.PersonJpaEntity;

public class MemberHomePersistenceMapper {

    public MemberHomeJpaEntity toEntity(UUID homeId, UUID personId, UUID roleId) {
        return new MemberHomeJpaEntity(
            new MemberHomeJpaEntityId(homeId, personId),
            roleId
        );
    }

    public MemberHomeDto toDto(MemberHomeJpaEntity memberHomeJpaEntity, PersonJpaEntity personJpaEntity, HomeJpaEntity homeJpaEntity) {
        return new MemberHomeDto(
            memberHomeJpaEntity.getId().getHomeId().toString(),
            memberHomeJpaEntity.getId().getPersonId().toString(),
            personJpaEntity.getName(),
            personJpaEntity.getLastName(),
            homeJpaEntity.getName(),
            personJpaEntity.getEmail(),
            personJpaEntity.isActive()
        );
    }
}
