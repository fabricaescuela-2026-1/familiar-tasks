package com.fabricaescuela.logs.application.service;

import com.fabricaescuela.logs.domain.model.Log;
import com.fabricaescuela.logs.domain.ports.in.CreateLogUseCase;
import com.fabricaescuela.logs.domain.ports.out.LogRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService implements CreateLogUseCase {
    private final LogRepositoryPort logRepository;

    @Override
    @Transactional
    public Log execute(String idUser, String modifiedElement, String action)
    {
        Log log = new Log(
                UUID.randomUUID().toString(),
                idUser,
                LocalDateTime.now(),
                modifiedElement,
                action
        );
        return logRepository.save(log);
    }

    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }
}
