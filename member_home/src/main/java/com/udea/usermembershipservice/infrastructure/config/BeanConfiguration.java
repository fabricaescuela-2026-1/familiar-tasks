package com.udea.usermembershipservice.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.udea.usermembershipservice.aplication.port.in.ICreateHomeUseCase;
import com.udea.usermembershipservice.aplication.port.in.ICreateRoleUseCase;
import com.udea.usermembershipservice.aplication.port.in.ICreatedMemberHome;
import com.udea.usermembershipservice.aplication.port.out.IAuditLogQueuePort;
import com.udea.usermembershipservice.aplication.port.out.IHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IMemberHomeRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.port.out.IRoleRepositoryPort;
import com.udea.usermembershipservice.infrastructure.adapter.out.audit.ServiceBusAuditLogQueueAdapter;
import com.udea.usermembershipservice.aplication.useCase.CreateMemberHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.CreatedHomeUseCase;
import com.udea.usermembershipservice.aplication.useCase.CreatedRoleUseCase;
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
    public ICreateRoleUseCase createRoleUseCase(
            IRoleRepositoryPort roleRepositoryPort,
            IPersonRepositoryPort personRepositoryPort
    ) {
        return new CreatedRoleUseCase(roleRepositoryPort, personRepositoryPort);
    }

    @Bean
    public ICreateHomeUseCase createHomeUseCase(
            IHomeRepositoryPort homeRepositoryPort,
            IPersonRepositoryPort personRepositoryPort,
            IRoleRepositoryPort roleRepositoryPort,
            IMemberHomeRepositoryPort memberHomeRepositoryPort
    ) {
        return new CreatedHomeUseCase(homeRepositoryPort, personRepositoryPort, roleRepositoryPort, memberHomeRepositoryPort);
    }

    @Bean
    public ICreatedMemberHome createdMemberHome(
            IHomeRepositoryPort homeRepositoryPort,
            IPersonRepositoryPort personRepositoryPort,
            IRoleRepositoryPort roleRepositoryPort,
            IMemberHomeRepositoryPort memberHomeRepositoryPort,
            IAuditLogQueuePort auditLogQueuePort
    ) {
        return new CreateMemberHomeUseCase(
            homeRepositoryPort,
            personRepositoryPort,
            roleRepositoryPort,
            memberHomeRepositoryPort,
            auditLogQueuePort
        );
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(destroyMethod = "close")
    public ServiceBusSenderClient auditLogSenderClient(
            @Value("${audit.log.servicebus.connection-string}") String connectionString,
            @Value("${audit.log.servicebus.queue-name}") String queueName
    ) {
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient();
    }

    @Bean
    public IAuditLogQueuePort auditLogQueuePort(ServiceBusSenderClient auditLogSenderClient) {
        return new ServiceBusAuditLogQueueAdapter(auditLogSenderClient);
    }
}
