package com.udea.usermembershipservice.aplication.port.out;

public interface IPasswordEncoderPort {
    public String encode(String password);
    public boolean matches(String password, String encodePassword);
}
