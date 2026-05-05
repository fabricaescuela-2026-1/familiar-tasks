package com.udea.usermembershipservice.aplication.useCase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.udea.usermembershipservice.aplication.port.out.IPersonRepositoryPort;
import com.udea.usermembershipservice.aplication.useCase.dto.queue.UserRegistrationEvent;
import com.udea.usermembershipservice.domain.model.Person;

@Service
public class UserSynchronizationService {

    private static final Logger logger = LoggerFactory.getLogger(UserSynchronizationService.class);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;

    private final IPersonRepositoryPort personRepositoryPort;

    public UserSynchronizationService(IPersonRepositoryPort personRepositoryPort) {
        this.personRepositoryPort = personRepositoryPort;
    }

    public void synchronizeUser(UserRegistrationEvent event) {
        try {
            UUID userId = UUID.fromString(event.userId());

            if (personRepositoryPort.getUserById(userId).isPresent()) {
                logger.warn("User {} already exists locally. Skipping synchronization.", userId);
                return;
            }

            LocalDateTime createdAt = LocalDateTime.parse(event.createdAt(), dateFormatter);

            Person person = Person.restore(
                userId,
                event.name(),
                event.lastname(),
                event.email(),
                event.passwordHash(),
                createdAt,
                true
            );

            personRepositoryPort.saveUser(person);
            logger.info("User {} synchronized successfully from authentication queue", userId);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format for user", e);
        } catch (Exception e) {
            logger.error("Error synchronizing user from event: {}", event, e);
        }
    }
}
