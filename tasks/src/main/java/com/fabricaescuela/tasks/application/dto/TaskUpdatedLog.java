package com.fabricaescuela.tasks.application.dto;

import java.util.UUID;

public record TaskUpdatedLog(
    UUID id,
    UUID idUser,
    String modifiedElement,
    String action
) {
    public static TaskUpdatedLog of(UUID userId, UUID taskId) {
        return new TaskUpdatedLog(UUID.randomUUID(), userId, taskId.toString(), "task_updated");
    }
}
