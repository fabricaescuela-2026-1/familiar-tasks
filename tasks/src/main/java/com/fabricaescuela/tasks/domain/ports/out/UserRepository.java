package com.fabricaescuela.tasks.domain.ports.out;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;



public interface UserRepository {

    Optional<User> findUserEntityByUsername(String username);


}
