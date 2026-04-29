package com.fabricaescuela.tasks.infraestructure.database.entyties;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GuestTaskId implements Serializable {
    private UUID guestId;
    private UUID taskId;
}