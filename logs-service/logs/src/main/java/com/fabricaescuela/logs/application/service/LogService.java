package com.fabricaescuela.logs.application.service;

import com.fabricaescuela.logs.domain.model.Log;
import com.fabricaescuela.logs.domain.ports.in.CreateLogUseCase;
import com.fabricaescuela.logs.domain.ports.out.LogRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService implements CreateLogUseCase {
    private final LogRepositoryPort logRepository;

    @Override
    @Transactional
    public Log execute(Log log) {
        return logRepository.save(log);
    }
}
