package com.udea.usermembershipservice.domain.model;

import java.util.UUID;

import com.udea.usermembershipservice.domain.exception.InvalidDataException;

public class Role {

    private UUID idRole;
    private String name;

    private Role(UUID idRole, String name){
        this.idRole = idRole;
        this.name = name;
    }

    public static Role create(UUID idRole, String name){
        validate(idRole, name);
        return new Role(idRole, name);
    }

    private static void validate(UUID idRole, String name) {
        if (idRole == null) {
            throw new InvalidDataException("Id role cannot be null.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be null or empty.");
        }
    }

    public static Role restore(UUID idRole, String name) {
        validate(idRole, name);
        return new Role(idRole, name);
    }

    public void changeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be null or empty.");
        }
        this.name = name;
    }

    public UUID getIdRole(){
        return idRole;
    }

    public String getName(){
        return name;
    }

}
