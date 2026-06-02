package com.fabricaescuela.tasks.application.dto;

import java.util.UUID;

public record TaskStatusChangedLog(
    UUID id,
    UUID idUser,
    String modifiedElement,
    String action,
    String newStatus
) {
    public static TaskStatusChangedLog of(UUID userId, UUID taskId, String newStatus) {
        return new TaskStatusChangedLog(
            UUID.randomUUID(), userId, taskId.toString(), "task_status_changed", newStatus);
    }
}
