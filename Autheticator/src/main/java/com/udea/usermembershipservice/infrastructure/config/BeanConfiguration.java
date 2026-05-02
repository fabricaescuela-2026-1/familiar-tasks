package com.udea.usermembershipservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.udea.usermembershipservice.aplication.port.in.ICreateHomeUseCase;
import com.udea.usermembershipservice.aplication.port.in.ICreateRoleUseCase;
import com.udea.usermembershipservice.aplication.port.in.ICreateUserUseCase;
import com.udea.usermembershipservice.aplication.port.in.ICreatedMemberHome;
import com.udea.usermembershipservice.aplication.port.in.ILoginUserCase;
import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPasswordEncoderPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.CreateMemberHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.CreatedHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.CreatedRoleUseCase;
import com.udea.usermembershipservice.aplication.useCase.CreatedUserUseCase;
import com.udea.usermembershipservice.aplication.useCase.LoginUserCase;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out.HomePersistenceAdapter;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out.MemberHomePersistenceAdapter;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out.PersonPersistenceAdapter;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.adapter.out.RolePersistenceAdapter;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.HomePersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.MemberHomePersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.PersonPersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.mapper.RolePersistenceMapper;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataHomeJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataMemberHomeJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.persistence.repository.SpringDataRoleJpaRepository;
import com.udea.usermembershipservice.infrastructure.adapter.out.security.PasswordEncoderAdapter;


@Configuration
public class BeanConfiguration {

    @Bean
    public PersonPersistenceMapper personPersistenceMapper() {
        return new PersonPersistenceMapper();
    }

    @Bean
    public RolePersistenceMapper rolePersistenceMapper() {
        return new RolePersistenceMapper();
    }

    @Bean
    public HomePersistenceMapper homePersistenceMapper() {
        return new HomePersistenceMapper();
    }

    @Bean
    public MemberHomePersistenceMapper memberHomePersistenceMapper() {
        return new MemberHomePersistenceMapper();
    }

    @Bean
    public IPersonRepositoryPort personRepositoryPort(
            SpringDataJpaRepository springDataJpaRepository,
            PersonPersistenceMapper personPersistenceMapper
    ) {
        return new PersonPersistenceAdapter(springDataJpaRepository, personPersistenceMapper);
    }

    @Bean
    public IRoleRepositoryPort roleRepositoryPort(
            SpringDataRoleJpaRepository springDataRoleJpaRepository,
            RolePersistenceMapper rolePersistenceMapper
    ) {
        return new RolePersistenceAdapter(springDataRoleJpaRepository, rolePersistenceMapper);
    }

    @Bean
    public IHomeRepositoryPort homeRepositoryPort(
            SpringDataHomeJpaRepository springDataHomeJpaRepository,
            HomePersistenceMapper homePersistenceMapper
    ) {
        return new HomePersistenceAdapter(springDataHomeJpaRepository, homePersistenceMapper);
    }

    @Bean
    public IMemberHomeRepositoryPort memberHomeRepositoryPort(
            SpringDataMemberHomeJpaRepository springDataMemberHomeJpaRepository,
            SpringDataJpaRepository springDataJpaRepository,
            MemberHomePersistenceMapper memberHomePersistenceMapper,
            SpringDataHomeJpaRepository springDataHomeJpaRepository
            
    ) {
        return new MemberHomePersistenceAdapter(
            springDataMemberHomeJpaRepository,
            springDataJpaRepository,
            memberHomePersistenceMapper,
            springDataHomeJpaRepository
        );
    }

    @Bean
    public IPasswordEncoderPort passwordEncoderPort() {
        return new PasswordEncoderAdapter();
    }

    @Bean
    public ICreateUserUseCase createUserUseCase(
            IPersonRepositoryPort personRepositoryPort,
            IPasswordEncoderPort passwordEncoderPort,
            ILoginUserCase loginUserCase
    ) {
        return new CreatedUserUseCase(personRepositoryPort, passwordEncoderPort, loginUserCase);
    }

    @Bean
    public ICreateRoleUseCase createRoleUseCase(
            IRoleRepositoryPort roleRepositoryPort,
            IPersonRepositoryPort personRepositoryPort
    ) {
        return new CreatedRoleUseCase(roleRepositoryPort, personRepositoryPort);
    }

    @Bean
    public ICreateHomeUseCase createHomeUseCase(
            IHomeRepositoryPort homeRepositoryPort,
            ILoginUserCase loginUserCase,
            IPersonRepositoryPort personRepositoryPort,
            IRoleRepositoryPort roleRepositoryPort,
            IMemberHomeRepositoryPort memberHomeRepositoryPort
    ) {
        return new CreatedHomeUseCase(homeRepositoryPort, loginUserCase, personRepositoryPort, roleRepositoryPort, memberHomeRepositoryPort);
    }

    @Bean
    public ILoginUserCase loginUserCase(
            IPersonRepositoryPort personRepositoryPort,
            IPasswordEncoderPort passwordEncoderPort
    ) {
        return new LoginUserCase(personRepositoryPort, passwordEncoderPort);
    }

    @Bean
    public ICreatedMemberHome createdMemberHome(
            IHomeRepositoryPort homeRepositoryPort,
            IPersonRepositoryPort personRepositoryPort,
            IRoleRepositoryPort roleRepositoryPort,
            IMemberHomeRepositoryPort memberHomeRepositoryPort
    ) {
        return new CreateMemberHomeUseCase(
            homeRepositoryPort,
            personRepositoryPort,
            roleRepositoryPort,
            memberHomeRepositoryPort
        );
    }
}
