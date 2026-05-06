package com.fabricaescuela.logs.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogMongoRepository extends MongoRepository<LogEntity,String> {
}
