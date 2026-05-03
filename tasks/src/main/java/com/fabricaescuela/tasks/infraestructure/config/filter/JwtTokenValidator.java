package com.fabricaescuela.tasks.infraestructure.config.filter;

import java.io.IOException;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fabricaescuela.tasks.infraestructure.adapter.out.AuthClient;
import com.fabricaescuela.tasks.infraestructure.util.JwtUtils;
import com.fabricaescuela.tasks.application.dto.AuthRefreshResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public class JwtTokenValidator extends OncePerRequestFilter{

    private final JwtUtils jwtUtils;
    private final AuthClient authClient;

    public JwtTokenValidator(JwtUtils jwtUtils, AuthClient authClient) {
        this.jwtUtils = jwtUtils;
        this.authClient = authClient;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);
                Claims claims = null;

                try {
                    claims = jwtUtils.validateToken(jwtToken);
                } catch (ExpiredJwtException e) {
                    log.info("Token expirado, intentando refrescar...");
                    claims = tryRefreshToken(request.getHeader("X-Refresh-Token"), response);
                    if (claims == null) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                if (claims != null) {
                    String username = jwtUtils.extractUsername(claims);
                    String roles = jwtUtils.getSpecificClaim(claims, "roles");
                    Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
                    SecurityContext context = SecurityContextHolder.getContext();
                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                }
            }
        } catch (Exception e) {
            log.severe("Error validating JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private Claims tryRefreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warning("Token expirado y no se proporciono refresh token");
            return null;
        }
        try {
            AuthRefreshResponse newTokens = authClient.refreshAccessToken(refreshToken);
            log.info("Token refrescado exitosamente");
            Claims claims = jwtUtils.validateToken(newTokens.getAccessToken());
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newTokens.getAccessToken());
            response.setHeader("X-Refresh-Token", newTokens.getRefreshToken());
            return claims;
        } catch (Exception e) {
            log.severe("Error al refrescar el token: " + e.getMessage());
            return null;
        }
    }

}
