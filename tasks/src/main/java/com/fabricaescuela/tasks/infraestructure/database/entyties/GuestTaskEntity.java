package com.fabricaescuela.tasks.infraestructure.database.entyties;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "guest_tasks")
public class GuestTaskEntity {
  @Id
  @Column(name = "guest_id", nullable = false)
  private UUID guestId;

  @Id
  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  private TaskEntity task;
}
