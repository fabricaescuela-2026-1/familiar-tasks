package com.udea.usermembershipservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.udea.usermembershipservice.domain.exception.InvalidDataException;

public class Person {

    private final UUID idPerson;
    private final String name;
    private final String lastName;
    private final String email;
    private final String passwordHash;
    private final LocalDateTime createdAt;
    private final Boolean active;

    private Person(UUID idPerson, String name, String lastName, String email, String passwordHash, LocalDateTime createdAt, Boolean active) {
        this.idPerson = idPerson;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.active = active;
    }

    public static Person restore(UUID idPerson, String name, String lastName, String email, String passwordHash, LocalDateTime createdAt, Boolean active) {
        if (idPerson == null) {
            throw new InvalidDataException("Id person cannot be null.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be null or empty.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidDataException("Last name cannot be null or empty.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidDataException("Email cannot be null or empty.");
        }
        if (createdAt == null) {
            throw new InvalidDataException("Created at cannot be null.");
        }
        if (active == null) {
            throw new InvalidDataException("Active cannot be null.");
        }
        return new Person(idPerson, name, lastName, email.toLowerCase(), passwordHash, createdAt, active);
    }

    public UUID getIdPerson() {
        return idPerson;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getcreatedAt() {
        return createdAt;
    }

    public Boolean getActive() {
        return active;
    }
}
