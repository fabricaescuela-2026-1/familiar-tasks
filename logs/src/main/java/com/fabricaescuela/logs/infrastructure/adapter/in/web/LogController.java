package com.fabricaescuela.logs.infrastructure.adapter.in.web;

import com.fabricaescuela.logs.application.dto.LogRequest;
import com.fabricaescuela.logs.application.dto.LogResponse;
import com.fabricaescuela.logs.application.mappers.LogMapper;
import com.fabricaescuela.logs.application.service.LogService;
import com.fabricaescuela.logs.domain.ports.in.CreateLogUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "Log API", description = "Endpoints for managing logs")
public class LogController {
    private final LogService logService;
    private final CreateLogUseCase createLogUseCase;
    private final LogMapper logMapper;

    @PostMapping
    @Operation(summary = "Create a new log entry", description = "Creates a new log entry with the provided details")
    public ResponseEntity<LogResponse> createLog(@Valid @RequestBody LogRequest logRequest){
        var savedLog = createLogUseCase.execute(
                logRequest.id(),
                logRequest.idUser(),
                logRequest.modifiedElement(),
                logRequest.action()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(logMapper.toResponse(savedLog));
    }

    @GetMapping
    @Operation(summary = "Get all log entries", description = "Retrieves a list of all log entries")
    public ResponseEntity<List<LogResponse>> getAllLogs(){
        List<LogResponse> responses = logService.getAllLogs()
                .stream()
                .map(logMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
