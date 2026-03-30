package com.fabricaescuela.tasks.infraestructure.database.entyties;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskEntity {
  @Id
  @Column(name = "task_id", nullable = false, unique = true, columnDefinition = "task_id UUID PRIMARY KEY DEFAULT gen_random_uuid()")
  private UUID taskId;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private String description;

  @ManyToOne
  @JoinColumn(name = "status_id", nullable = false)
  private StatusEntity status;

  @ManyToOne
  @JoinColumn(name = "priority_id", nullable = false)
  private PriorityEntity priority;

  @Column(name = "home_id", nullable = false)
  private UUID homeId;

  @Column(name = "guest_id")
  private UUID guestId;

  @Column(name = "created_at", columnDefinition = "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime deadline;
}
