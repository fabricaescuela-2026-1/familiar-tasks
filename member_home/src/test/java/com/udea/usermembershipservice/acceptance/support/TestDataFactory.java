package com.udea.usermembershipservice.acceptance.support;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.HomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntityId;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.PersonJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.RoleJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataHomeJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataMemberHomeJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataRoleJpaRepository;

@Component
public class TestDataFactory {

  @Autowired private SpringDataJpaRepository personRepo;
  @Autowired private SpringDataRoleJpaRepository roleRepo;
  @Autowired private SpringDataHomeJpaRepository homeRepo;
  @Autowired private SpringDataMemberHomeJpaRepository memberHomeRepo;

  public PersonJpaEntity createPerson(String email) {
    PersonJpaEntity p = new PersonJpaEntity(
        UUID.randomUUID(),
        "Nombre",
        "Apellido",
        email.toLowerCase(),
        "hash",
        LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0, 0),
        true
    );
    return personRepo.save(p);
  }

  public RoleJpaEntity ensureRole(String name) {
    return roleRepo.findByNameIgnoreCase(name).orElseGet(() -> {
      RoleJpaEntity r = new RoleJpaEntity(UUID.randomUUID(), name);
      return roleRepo.save(r);
    });
  }

  public HomeJpaEntity createHome(String name) {
    HomeJpaEntity h = new HomeJpaEntity(UUID.randomUUID(), name, LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0, 0));
    return homeRepo.save(h);
  }

  public void linkMember(UUID homeId, UUID personId, UUID roleId) {
    MemberHomeJpaEntity mh = new MemberHomeJpaEntity(
        new MemberHomeJpaEntityId(homeId, personId),
        roleId
    );
    memberHomeRepo.save(mh);
  }

  public SpringDataJpaRepository persons() { return personRepo; }
  public SpringDataRoleJpaRepository roles() { return roleRepo; }
  public SpringDataHomeJpaRepository homes() { return homeRepo; }
  public SpringDataMemberHomeJpaRepository members() { return memberHomeRepo; }
}
