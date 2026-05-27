package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntityId;

public interface SpringDataMemberHomeJpaRepository extends JpaRepository<MemberHomeJpaEntity, MemberHomeJpaEntityId> {

    Optional<MemberHomeJpaEntity> findByIdPersonId(UUID personId);

    List<MemberHomeJpaEntity> findAllByIdHomeId(UUID homeId);

    void deleteByIdHomeIdAndIdPersonId(UUID homeId, UUID personId);

    Optional<MemberHomeJpaEntity> findByIdPersonIdAndIdHomeId(UUID personId, UUID homeId);
    @Query("SELECT m.id.homeId FROM MemberHomeJpaEntity m WHERE m.id.personId = :personId")
    List<UUID> findAllHomeIdsByIdPersonId(@Param("personId") UUID personId);

    @Query("SELECT m.roleId FROM MemberHomeJpaEntity m WHERE m.id.personId = :personId")
    List<UUID> findAllRoleIdsByIdPersonId(@Param("personId") UUID personId);
}
