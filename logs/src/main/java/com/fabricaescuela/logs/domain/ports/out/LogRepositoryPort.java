package com.fabricaescuela.logs.domain.ports.out;

import com.fabricaescuela.logs.domain.model.Log;

import java.util.List;

public interface LogRepositoryPort {
    Log save(Log log);
    List<Log> findAll();
}
