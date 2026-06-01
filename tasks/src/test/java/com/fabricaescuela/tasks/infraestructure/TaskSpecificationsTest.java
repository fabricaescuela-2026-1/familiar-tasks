package com.fabricaescuela.tasks.infraestructure;

import com.fabricaescuela.tasks.infraestructure.database.entyties.TaskEntity;
import com.fabricaescuela.tasks.infraestructure.database.specifications.TaskSpecifications;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskSpecificationsTest {

    // keyword null debe retornar null para que Specification.where() no filtre
    @Test
    void keywordNuloRetornaNull() {
        // Act
        Specification<TaskEntity> spec = TaskSpecifications.nameOrDescriptionContains(null);

        // Assert
        assertNull(spec);
    }

    // keyword en blanco debe retornar null igual que null
    @Test
    void keywordBlankRetornaNull() {
        // Act
        Specification<TaskEntity> spec = TaskSpecifications.nameOrDescriptionContains("   ");

        // Assert
        assertNull(spec);
    }

    // keyword con valor genera predicado no nulo listo para aplicar LIKE
    @Test
    void keywordConValorRetornaEspecificacionNoNula() {
        // Act
        Specification<TaskEntity> spec = TaskSpecifications.nameOrDescriptionContains("barrer");

        // Assert
        assertNotNull(spec);
    }
}