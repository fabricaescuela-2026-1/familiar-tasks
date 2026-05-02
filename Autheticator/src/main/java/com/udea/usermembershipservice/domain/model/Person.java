package com.udea.usermembershipservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.udea.usermembershipservice.domain.exception.InvalidEmailException;
import com.udea.usermembershipservice.domain.exception.InvalidPasswordException;
import com.udea.usermembershipservice.domain.exception.InvalidDataException;

public class Person {

    private UUID idPerson;
    private String name;
    private String  lastName;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private Boolean active;

    private Person(UUID idPerson, String name, String lastName, String email, String password, LocalDateTime createdAt, Boolean active) {
        this.idPerson = idPerson;
        this.name = name;
        this.lastName =  lastName;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.active = active;
    }

    public static Person create(String name, String lastName, String email, String password, LocalDateTime createdAt, Boolean active) {
        validate(name, lastName, email, password, createdAt, active);
        return new Person(UUID.randomUUID(), name, lastName, email.toLowerCase(), password, createdAt, active);
    }

    public static void validate(String name, String lastName, String email, String password, LocalDateTime createdAt, Boolean active) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be null or empty.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidDataException("Last name cannot be null or empty.");
        }
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailException();
        }
        if (password == null || password.trim().isEmpty() || password.length() < 8 || password.chars().noneMatch(c -> !Character.isLetterOrDigit(c))) {
            throw new InvalidPasswordException();
        }
        if (createdAt == null) {
            throw new InvalidDataException("Created at cannot be null.");
        }
        if (active == null) {
            throw new InvalidDataException("Active cannot be null.");
        }
    }

    public static Person restore(UUID idPerson, String name, String lastName, String email, String password, LocalDateTime createdAt, Boolean active) {
        return new Person(idPerson, name, lastName, email, password, createdAt, active);
    }

    public void changeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be null or empty.");
        }
        this.name = name;
    }

    public void changeLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidDataException("Last name cannot be null or empty.");
        }
        this.lastName = lastName;
    }

    public void changeEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailException();
        }
        this.email = email.toLowerCase();
    }

    public void changePassword(String password) {
        if (password == null || password.trim().isEmpty() || password.length() < 8 || password.chars().noneMatch(c -> !Character.isLetterOrDigit(c))) {
            throw new InvalidPasswordException();
        }
        this.password = password;
    }

    public void changeActive(Boolean active) {
        if (active == null) {
            throw new InvalidDataException("Active cannot be null.");
        }
        this.active = active;
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


    public String getPassword() {
        return password;
    }

    public LocalDateTime getcreatedAt() {
        return createdAt;
    }


    public Boolean getActive() {
        return active;
    }
}



