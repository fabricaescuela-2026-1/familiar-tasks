package com.fabricaescuela.tasks.infraestructure.database.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.fabricaescuela.tasks.infraestructure.database.entyties.TaskEntity;

public class TaskSpecifications {

  private TaskSpecifications() {}

  public static Specification<TaskEntity> nameOrDescriptionContains(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return (root, query, builder) -> builder.conjunction();
    }
    String patron = "%" + keyword.toLowerCase() + "%";
    return (root, query, builder) -> builder.or(
        builder.like(builder.lower(root.get("name")), patron),
        builder.like(builder.lower(root.get("description")), patron));
  }
}