package com.fabricaescuela.logs.infrastructure.adapter.in.web;

import com.fabricaescuela.logs.application.dto.LogRequest;
import com.fabricaescuela.logs.application.dto.LogResponse;
import com.fabricaescuela.logs.application.mappers.LogMapper;
import com.fabricaescuela.logs.application.service.LogService;
import com.fabricaescuela.logs.domain.ports.in.CreateLogUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;
    private final CreateLogUseCase createLogUseCase;
    private final LogMapper logMapper;

    @PostMapping
    public ResponseEntity<LogResponse> createLog(@RequestBody LogRequest logRequest){
        var savedLog = createLogUseCase.execute(
                logRequest.idUser(),
                logRequest.modifiedElement(),
                logRequest.action()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(logMapper.toResponse(savedLog));
    }

    @GetMapping
    public ResponseEntity<List<LogResponse>> getAllLogs(){
        List<LogResponse> responses = logService.getAllLogs()
                .stream()
                .map(logMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
