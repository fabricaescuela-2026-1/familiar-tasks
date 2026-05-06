package com.fabricaescuela.logs.domain.ports.in;

import com.fabricaescuela.logs.domain.model.Log;

public interface CreateLogUseCase {
    Log execute(String id, String idUser, String modifiedElement, String action);
}
