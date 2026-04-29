package com.fabricaescuela.tasks.infraestructure.config.filter;

import java.io.IOException;
import java.security.Security;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fabricaescuela.tasks.infraestructure.util.JwtUtils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

public class JwtTokenValidator extends OncePerRequestFilter{

    private final JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)throws ServletException, IOException {

        try {
            String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

            if(jwtToken != null && jwtToken.startsWith("Bearer ")){
                jwtToken = jwtToken.substring(7);

                Claims claims = jwtUtils.validateToken(jwtToken);

                String username = jwtUtils.extractUsername(claims);

                String roles = jwtUtils.getSpecificClaim(claims, "roles");

                Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);

                SecurityContext context = SecurityContextHolder.getContext();

                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        } catch (Exception e) {
            System.out.println("Error validating JWT: " + e.getMessage());
        }
        
        filterChain.doFilter(request, response);

    }

}
