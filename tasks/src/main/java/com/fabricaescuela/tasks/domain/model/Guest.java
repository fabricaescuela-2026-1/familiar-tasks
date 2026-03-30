package com.fabricaescuela.tasks.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Guest {
  private String name;
  private String lastname;
  private String email;
  private String active;
}
