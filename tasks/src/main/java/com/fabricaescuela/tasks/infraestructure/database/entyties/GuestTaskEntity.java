package com.fabricaescuela.tasks.infraestructure.database.entyties;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "guest_tasks")
@IdClass(GuestTaskId.class)
public class GuestTaskEntity {
  @Id
  @Column(name = "guest_id", nullable = false)
  private UUID guestId;

  @Id
  @Column(name = "task_id", nullable = false)
  private UUID taskId;
}
