package com.fabricaescuela.logs.application.mappers;

import com.fabricaescuela.logs.application.dto.LogResponse;
import com.fabricaescuela.logs.domain.model.Log;
import org.springframework.stereotype.Component;

@Component
public class LogMapper {

    public LogResponse toResponse(Log log) {
        return new LogResponse(
                log.id(),
                log.idUser(),
                log.timestamp(),
                log.modifiedElement(),
                log.action()
        );
    }
}
