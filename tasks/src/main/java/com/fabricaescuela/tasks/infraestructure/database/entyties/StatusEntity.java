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
@Table(name = "status")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StatusEntity {
  @Id
  @Column(name = "status_id", nullable = false, columnDefinition = " status_id UUID PRIMARY KEY DEFAULT gen_random_uuid()")
  private UUID statusId;

  @Column(name = "name", nullable = false, length = 50)
  private String name;
}
