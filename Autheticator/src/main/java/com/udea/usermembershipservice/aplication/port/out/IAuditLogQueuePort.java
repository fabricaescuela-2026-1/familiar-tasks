package com.udea.usermembershipservice.aplication.port.out;

import java.util.UUID;

public interface IAuditLogQueuePort {
    void publishRoleChanged(UUID userId, String modifiedElement);
}
