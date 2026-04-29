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
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)throws ServletException, IOException {

        try {
            String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

            if(jwtToken != null && jwtToken.startsWith("Bearer ")){
                jwtToken = jwtToken.substring(7);

                Claims claims = null;
                
                // Intentar validar el token
                try {
                    claims = jwtUtils.validateToken(jwtToken);
                } catch (ExpiredJwtException e) {
                    // Token expirado - intentar refrescar
                    log.info("Token expirado, intentando refrescar...");
                    String refreshToken = request.getHeader("X-Refresh-Token");
                    
                    if (refreshToken != null && !refreshToken.isEmpty()) {
                        try {
                            AuthRefreshResponse newTokens = authClient.refreshAccessToken(refreshToken);
                            log.info("Token refrescado exitosamente");
                            
                            // Obtener claims del nuevo token
                            claims = jwtUtils.validateToken(newTokens.getAccessToken());
                            
                            // Actualizar el header de respuesta con el nuevo token
                            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newTokens.getAccessToken());
                            response.setHeader("X-Refresh-Token", newTokens.getRefreshToken());
                            
                        } catch (Exception refreshError) {
                            log.severe("Error al refrescar el token: " + refreshError.getMessage());
                            // Continuar sin autenticación
                            filterChain.doFilter(request, response);
                            return;
                        }
                    } else {
                        log.warning("Token expirado y no se proporciono refresh token");
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

}
