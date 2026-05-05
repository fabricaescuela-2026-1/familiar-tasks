package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.PersonJpaEntity;

public interface SpringDataJpaRepository extends JpaRepository<PersonJpaEntity, UUID> {
    public Optional<PersonJpaEntity> findByEmail(String email);
    

}
