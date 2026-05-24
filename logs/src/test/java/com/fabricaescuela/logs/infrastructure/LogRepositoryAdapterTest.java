package com.fabricaescuela.logs.infrastructure;

import com.fabricaescuela.logs.domain.model.Log;
import com.fabricaescuela.logs.infrastructure.adapter.out.LogRepositoryAdapter;
import com.fabricaescuela.logs.infrastructure.adapter.out.persistence.LogEntity;
import com.fabricaescuela.logs.infrastructure.adapter.out.persistence.LogMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogRepositoryAdapterTest {

    @Mock private LogMongoRepository mongoRepository;

    @InjectMocks
    private LogRepositoryAdapter adapter;

    // ── CASOS DE ÉXITO ──────────────────────────────────────────────────────

    @Test
    void savePersisteEntidadYRetornaDominio() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now();
        Log log = new Log(id, userId, timestamp, "TASK", "CREATED");
        LogEntity entity = new LogEntity(id, userId, timestamp, "TASK", "CREATED");
        when(mongoRepository.save(any(LogEntity.class))).thenReturn(entity);

        // Act
        Log resultado = adapter.save(log);

        // Assert
        assertEquals(id, resultado.id());
        assertEquals(userId, resultado.idUser());
        assertEquals("TASK", resultado.modifiedElement());
        verify(mongoRepository).save(any(LogEntity.class));
    }

    @Test
    void findAllRetornaListaDeDominio() {
        // Arrange
        LogEntity e1 = new LogEntity("1", "u1", LocalDateTime.now(), "TASK", "CREATED");
        LogEntity e2 = new LogEntity("2", "u2", LocalDateTime.now(), "ROLE", "CHANGED");
        when(mongoRepository.findAll()).thenReturn(List.of(e1, e2));

        // Act
        List<Log> resultado = adapter.findAll();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("1", resultado.get(0).id());
        assertEquals("ROLE", resultado.get(1).modifiedElement());
    }

    @Test
    void findAllConRepositorioVacioRetornaListaVacia() {
        // Arrange
        when(mongoRepository.findAll()).thenReturn(List.of());

        // Act
        List<Log> resultado = adapter.findAll();

        // Assert
        assertTrue(resultado.isEmpty());
    }
}
