package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper;

import com.udea.usermembershipservice.domain.model.Home;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.HomeJpaEntity;

public class HomePersistenceMapper {

    public HomeJpaEntity toEntity(Home home) {
        return new HomeJpaEntity(
            home.getIdHome(),
            home.getName(),
            home.getCreatedAt()
        );
    }

    public Home toDomain(HomeJpaEntity homeJpaEntity) {
        return Home.restore(
            homeJpaEntity.getId(),
            homeJpaEntity.getName(),
            homeJpaEntity.getCreatedAt()
        );
    }
}
