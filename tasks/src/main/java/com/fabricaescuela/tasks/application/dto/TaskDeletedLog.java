package com.fabricaescuela.tasks.application.dto;

import java.util.UUID;

public record TaskDeletedLog(
    UUID id,
    UUID idUser,
    String modifiedElement,
    String action
) {
    public static TaskDeletedLog of(UUID userId, UUID taskId) {
        return new TaskDeletedLog(UUID.randomUUID(), userId, taskId.toString(), "task_deleted");
    }
}
