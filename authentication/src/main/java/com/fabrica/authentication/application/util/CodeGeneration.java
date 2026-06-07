package com.fabrica.authentication.application.util;

import java.security.SecureRandom;

public final class CodeGeneration {

  private static final SecureRandom RANDOM = new SecureRandom();

  private CodeGeneration() {}

  public static String generateSixDigitCode() {
    return String.format("%06d", RANDOM.nextInt(1_000_000));
  }
}
