package com.fabricaescuela.tasks.infraestructure.presentation.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemDetails {
  private String type;
  private String title;
  private int status;
  private String detail;
  private String instance;
}
