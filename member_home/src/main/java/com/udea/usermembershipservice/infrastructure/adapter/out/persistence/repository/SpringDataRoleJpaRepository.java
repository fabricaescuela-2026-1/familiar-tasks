package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;

public interface SpringDataRoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {

    Optional<RoleJpaEntity> findByNameIgnoreCase(String name);
}
