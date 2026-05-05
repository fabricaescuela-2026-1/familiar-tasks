package com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberDto;
import com.udea.usermembershipservice.aplication.useCase.dto.mermberHome.MemberHomeDto;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntity;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.entity.MemberHomeJpaEntityId;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.MemberHomePersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataHomeJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataMemberHomeJpaRepository;

@Transactional
public class MemberHomePersistenceAdapter implements IMemberHomeRepositoryPort {

    private final SpringDataMemberHomeJpaRepository repository;
    private final SpringDataJpaRepository personRepository;
    private final MemberHomePersistenceMapper mapper;
    private final SpringDataHomeJpaRepository homeRepository;

    public MemberHomePersistenceAdapter(
            SpringDataMemberHomeJpaRepository repository,
            SpringDataJpaRepository personRepository,
            MemberHomePersistenceMapper mapper,
            SpringDataHomeJpaRepository homeRepository) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.mapper = mapper;
        this.homeRepository = homeRepository;
    }

    @Override
    public void saveMemberHome(UUID homeId, UUID personId, UUID rol) {
        MemberHomeJpaEntity savedMemberHome = repository.save(mapper.toEntity(homeId, personId, rol));
        if (savedMemberHome == null) {
            throw new IllegalStateException("Error saving member home");
        }
    }

    @Override
    public void deleteMemberHome(UUID homeId, UUID personId) {
        repository.deleteByIdHomeIdAndIdPersonId(homeId, personId);
    }

    @Override
    public Optional<MemberHomeDto> getMemberHome(UUID personId, UUID homeId) {
    return repository.findByIdPersonIdAndIdHomeId(personId, homeId)
        .flatMap(memberHomeJpaEntity ->
            personRepository.findById(personId)
                .flatMap(personJpaEntity ->
                    homeRepository.findById(homeId)
                        .map(homeJpaEntity ->
                            mapper.toDto(memberHomeJpaEntity, personJpaEntity, homeJpaEntity)
                        )
                )
        );
}

    @Override
    public List<MemberDto> getAllMemberHome(UUID homeId) {
        return repository.findAllByIdHomeId(homeId).stream()
            .flatMap(memberHomeJpaEntity -> personRepository.findById(memberHomeJpaEntity.getId().getPersonId()).stream()
                .map(personJpaEntity -> new MemberDto(
                    memberHomeJpaEntity.getId().getPersonId(),
                    personJpaEntity.getName(),
                    personJpaEntity.getLastName(),
                    personJpaEntity.getEmail(),
                    memberHomeJpaEntity.getRoleId()
                )))
            .toList();
    }

    @Override
    @Transactional
    public void updateRoleMemberHome(UUID homeId, UUID personId, UUID newRol) {
        MemberHomeJpaEntityId memberHomeId = new MemberHomeJpaEntityId(homeId, personId);
        MemberHomeJpaEntity memberHome = repository.findById(memberHomeId)
            .orElseThrow(() -> new RuntimeException("Member home not found"));
        memberHome.setRoleId(newRol);
        repository.save(memberHome);
    }
}
