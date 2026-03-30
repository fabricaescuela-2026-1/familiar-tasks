package com.fabricaescuela.tasks.infraestructure.database.entyties;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "priorities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PriorityEntity {
  @Id
  @Column(name = "priority_id", nullable = false, columnDefinition = " priority_id UUID PRIMARY KEY DEFAULT gen_random_uuid()")
  private UUID priorityId;
  @Column(name = "name", nullable = false)
  private String name;
}
