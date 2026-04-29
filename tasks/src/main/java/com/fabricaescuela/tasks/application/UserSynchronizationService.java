package com.fabricaescuela.tasks.application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fabricaescuela.tasks.application.dto.UserRegistrationEvent;
import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;
import com.fabricaescuela.tasks.infraestructure.database.jpa.UserRepository;

@Service
public class UserSynchronizationService {

    private static final Logger logger = LoggerFactory.getLogger(UserSynchronizationService.class);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;

    private final UserRepository userRepository;

    public UserSynchronizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void synchronizeUser(UserRegistrationEvent event) {
        try {
            UUID userId = UUID.fromString(event.userId());

            // Verificar si el usuario ya existe
            Optional<UserEntity> existingUser = userRepository.findById(userId);
            if (existingUser.isPresent()) {
                logger.warn("User {} already exists in the database. Skipping synchronization.", userId);
                return;
            }

            // Convertir el evento a entidad
            LocalDateTime createdAt = LocalDateTime.parse(event.createdAt(), dateFormatter);

            UserEntity userEntity = UserEntity.builder()
                    .userId(userId)
                    .name(event.name())
                    .lastname(event.lastname())
                    .email(event.email())
                    .passwordHash(event.passwordHash())
                    .isActive(true)
                    .createdAt(createdAt)
                    .build();

            // Guardar el usuario en la base de datos
            userRepository.save(userEntity);
            logger.info("User {} synchronized successfully from authentication service", userId);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format for user", e);
        } catch (Exception e) {
            logger.error("Error synchronizing user from event: {}", event, e);
        }
    }
}
