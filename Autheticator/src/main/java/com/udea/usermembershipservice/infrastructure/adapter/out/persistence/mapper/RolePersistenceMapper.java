package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper;

import com.udea.usermembershipservice.domain.model.Role;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;

public class RolePersistenceMapper {

    public RoleJpaEntity toEntity(Role role) {
        return new RoleJpaEntity(
            role.getIdRole(),
            role.getName()
        );
    }

    public Role toDomain(RoleJpaEntity roleJpaEntity) {
        return Role.restore(
            roleJpaEntity.getId(),
            roleJpaEntity.getName()
        );
    }
}
