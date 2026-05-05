package com.udea.usermembershipservice.domain.model;

import com.udea.usermembershipservice.domain.exception.InvalidDataException;

public class MemberHome {
    
    private Home home;
    private Role role;
    private Person person;

    private MemberHome(Home home, Role role, Person person){
        this.home = home;
        this.role = role;
        this.person = person;
    }

    public static MemberHome create(Home home, Role role, Person person){
        validate(home, role, person);
        return new MemberHome(home, role, person);
    }

    public static void validate(Home home, Role role, Person person) {
        if (home == null) {
            throw new InvalidDataException("Home cannot be null.");
        }
        if (role == null) {
            throw new InvalidDataException("Role cannot be null.");
        }
        if (person == null) {
            throw new InvalidDataException("Person cannot be null.");
        }
    }

    public static MemberHome restore(Home home, Role role, Person person) {
        return new MemberHome(home, role, person);
    }

    public void changeHome(Home home) {
        if (home == null) {
            throw new InvalidDataException("Home cannot be null.");
        }
        this.home = home;
    }

    public void changeRole(Role role) {
        if (role == null) {
            throw new InvalidDataException("Role cannot be null.");
        }
        this.role = role;
    }

    public void changePerson(Person person) {
        if (person == null) {
            throw new InvalidDataException("Person cannot be null.");
        }
        this.person = person;
    }

    public Home getHome(){
        return home;
    }

    public Role getRole(){
        return role;
    }

    public Person getPerson(){
        return person;
    }
}
