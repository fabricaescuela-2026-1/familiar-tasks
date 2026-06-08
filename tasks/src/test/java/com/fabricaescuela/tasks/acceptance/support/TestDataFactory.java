package com.fabricaescuela.tasks.acceptance.support;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fabricaescuela.tasks.infraestructure.database.PriorityJpaRepository;
import com.fabricaescuela.tasks.infraestructure.database.StatusJpaRepository;
import com.fabricaescuela.tasks.infraestructure.database.TaskJpaRepository;
import com.fabricaescuela.tasks.infraestructure.database.entyties.PriorityEntity;
import com.fabricaescuela.tasks.infraestructure.database.entyties.StatusEntity;
import com.fabricaescuela.tasks.infraestructure.database.entyties.TaskEntity;
import com.fabricaescuela.tasks.infraestructure.database.entyties.UserEntity;
import com.fabricaescuela.tasks.infraestructure.database.jpa.UserRepository;

@Component
public class TestDataFactory {

  @Autowired private TaskJpaRepository taskRepo;
  @Autowired private StatusJpaRepository statusRepo;
  @Autowired private PriorityJpaRepository priorityRepo;
  @Autowired private UserRepository userRepo;

  public StatusEntity ensureStatus(String name) {
    return statusRepo.findByName(name).orElseGet(() ->
        statusRepo.save(StatusEntity.builder().statusId(UUID.randomUUID()).name(name).build())
    );
  }

  public PriorityEntity ensurePriority(String name) {
    return priorityRepo.findByName(name).orElseGet(() ->
        priorityRepo.save(PriorityEntity.builder().priorityId(UUID.randomUUID()).name(name).build())
    );
  }

  public UserEntity createGuest(String email) {
    UserEntity g = UserEntity.builder()
        .userId(UUID.randomUUID())
        .name("Guest")
        .lastname("Test")
        .email(email)
        .passwordHash("hash")
        .isActive(true)
        .createdAt(LocalDateTime.of(2099, Month.JANUARY, 1, 10, 0, 0))
        .build();
    return userRepo.save(g);
  }

  public TaskEntity createTask(String name, String description, StatusEntity status, PriorityEntity priority,
                               UUID homeId, UUID guestId, LocalDateTime deadline) {
    TaskEntity t = TaskEntity.builder()
        .taskId(UUID.randomUUID())
        .name(name)
        .description(description)
        .status(status)
        .priority(priority)
        .homeId(homeId)
        .guestId(guestId)
        .createdAt(LocalDateTime.of(2099, Month.JANUARY, 1, 10, 0, 0))
        .deadline(deadline)
        .build();
    return taskRepo.save(t);
  }

  public TaskJpaRepository tasks() { return taskRepo; }
  public StatusJpaRepository statuses() { return statusRepo; }
  public PriorityJpaRepository priorities() { return priorityRepo; }
  public UserRepository guests() { return userRepo; }
}
