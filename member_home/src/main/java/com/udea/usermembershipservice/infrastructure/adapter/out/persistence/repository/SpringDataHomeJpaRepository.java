package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.HomeJpaEntity;

public interface SpringDataHomeJpaRepository extends JpaRepository<HomeJpaEntity, UUID> {

    Optional<HomeJpaEntity> findByNameIgnoreCase(String name);
}
