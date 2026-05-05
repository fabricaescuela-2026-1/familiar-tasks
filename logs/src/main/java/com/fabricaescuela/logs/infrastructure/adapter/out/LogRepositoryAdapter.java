package com.fabricaescuela.logs.infrastructure.adapter.out;

import com.fabricaescuela.logs.domain.model.Log;
import com.fabricaescuela.logs.domain.ports.out.LogRepositoryPort;
import com.fabricaescuela.logs.infrastructure.adapter.out.persistence.LogEntity;
import com.fabricaescuela.logs.infrastructure.adapter.out.persistence.LogMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LogRepositoryAdapter implements LogRepositoryPort {
    private final LogMongoRepository mongoRepository;

    @Override
    public Log save(Log log) {
        LogEntity logEntity = mapToEntity(log);
        LogEntity savedLogEntity = mongoRepository.save(logEntity);
        return mapToDomain(savedLogEntity);
    }

    @Override
    public List <Log> findAll() {
        return mongoRepository.findAll()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    private LogEntity mapToEntity(Log log) {
        return new LogEntity(
                log.id(),
                log.idUser(),
                log.timestamp(),
                log.modifiedElement(),
                log.action()
        );
    }

    private Log mapToDomain(LogEntity entity) {
        return new Log(
                entity.id(),
                entity.idUser(),
                entity.timestamp(),
                entity.modifiedElement(),
                entity.action()
        );
    }
}
