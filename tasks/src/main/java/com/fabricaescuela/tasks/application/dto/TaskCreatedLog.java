package com.fabricaescuela.tasks.application.dto;

import java.util.UUID;

public record TaskCreatedLog(
    UUID id,
    UUID idUser,
    String modifiedElement,
    String action
) {
    public static TaskCreatedLog taskCreated(UUID userId, UUID taskId) {
        return new TaskCreatedLog(UUID.randomUUID(), userId, taskId.toString(), "task_created");
    }
}
