package com.fabricaescuela.logs.domain.ports.in;

import com.fabricaescuela.logs.domain.model.Log;

import java.time.LocalDateTime;

public interface CreateLogUseCase {
    Log execute(String id, String idUser, LocalDateTime timeStamp, String modifiedElement, String action);
}
