package com.fabricaescuela.tasks.domain.ports.out;

import java.util.UUID;

public interface TaskAuditLogPort {
    void publishTaskCreated(UUID userId, UUID taskId);
}
