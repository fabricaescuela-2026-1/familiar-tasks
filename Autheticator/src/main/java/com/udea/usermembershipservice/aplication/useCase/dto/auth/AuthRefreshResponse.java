package com.udea.usermembershipservice.aplication.useCase.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRefreshResponse {
    private String accessToken;
    private String refreshToken;
}
