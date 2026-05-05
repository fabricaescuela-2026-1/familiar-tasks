package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntityId;

public interface SpringDataMemberHomeJpaRepository extends JpaRepository<MemberHomeJpaEntity, MemberHomeJpaEntityId> {

    Optional<MemberHomeJpaEntity> findByIdPersonId(UUID personId);

    List<MemberHomeJpaEntity> findAllByIdHomeId(UUID homeId);

    void deleteByIdHomeIdAndIdPersonId(UUID homeId, UUID personId);

    Optional<MemberHomeJpaEntity> findByIdPersonIdAndIdHomeId(UUID personId, UUID homeId);
}
