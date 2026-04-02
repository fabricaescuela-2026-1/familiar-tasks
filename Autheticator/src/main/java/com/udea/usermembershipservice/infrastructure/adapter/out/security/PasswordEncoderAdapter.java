package com.udea.usermembershipservice.infrastructure.adapter.out.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.udea.usermembershipservice.aplication.port.out.IPasswordEncoderPort;

public class PasswordEncoderAdapter implements IPasswordEncoderPort{

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }
    
    @Override
    public boolean matches(String password, String encodePassword) {
        return passwordEncoder.matches(password, encodePassword);
    }
}

