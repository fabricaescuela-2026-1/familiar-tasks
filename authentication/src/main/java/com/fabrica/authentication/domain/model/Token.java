package com.fabrica.authentication.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Token {
  private UUID tokenId;
  private String tokenHash;
  private LocalDateTime expirationDate;
  private LocalDateTime expiratedAt;
  private String tokenType;
  private User user;
}
