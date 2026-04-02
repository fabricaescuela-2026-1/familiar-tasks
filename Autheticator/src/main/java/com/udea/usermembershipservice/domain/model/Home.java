package com.udea.usermembershipservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.udea.usermembershipservice.domain.exception.InvalidDataException;

public class Home {
    
    private UUID idHome;
    private String name;
    private LocalDateTime createdAt;

    private Home(UUID idHome, String name, LocalDateTime createdAt){
        this.idHome = idHome;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static Home create(UUID idHome, String name, LocalDateTime createdAt){
        validate(idHome, name, createdAt);
        return new Home(idHome, name, createdAt);
    }

    private static void validate(UUID idHome, String name, LocalDateTime createdAt) {
        if (idHome == null) {
            throw new InvalidDataException("Id home cannot be null.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be null or empty.");
        }
        if (createdAt == null) {
            throw new InvalidDataException("Created at cannot be null.");
        }
    }

    public static Home restore(UUID idHome, String name, LocalDateTime createdAt) {
        return new Home(idHome, name, createdAt);
    }

    public void changeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be null or empty.");
        }
        this.name = name;
    }

    public UUID getIdHome(){
        return idHome;
    }

    public String getName(){
        return name;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
}
