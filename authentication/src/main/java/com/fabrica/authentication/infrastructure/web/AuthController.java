package com.fabrica.authentication.infrastructure.web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @PostMapping("/login")
  public String login() {
    return null;
  }

  @PostMapping("/register")
  public String register() {
    return null;
  }

  @PostMapping("/refresh")
  public String refresh() {
    return null;
  }

}
