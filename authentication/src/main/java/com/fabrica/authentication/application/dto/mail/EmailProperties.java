package com.fabrica.authentication.application.dto.mail;

import lombok.Builder;

@Builder
public record EmailProperties(String code, String recipient, String subject) {}
