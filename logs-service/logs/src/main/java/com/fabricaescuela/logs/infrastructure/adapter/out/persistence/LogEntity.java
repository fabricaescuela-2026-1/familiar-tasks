package com.fabricaescuela.logs.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "logs")
public record LogEntity (
        @Id
        String id,
        @Field("id_user")
        String idUser,
        @Field("timestamp")
        LocalDateTime timestamp,
        @Field("modified_Element")
        String modifiedElement,
        @Field("action")
        String action
) {}
