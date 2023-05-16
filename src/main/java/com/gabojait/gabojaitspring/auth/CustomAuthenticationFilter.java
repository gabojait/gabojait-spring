package com.gabojait.gabojaitspring.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, JWTVerificationException {
        String token = request.getHeader(AUTHORIZATION) == null ?
                request.getHeader("Refresh-Token") : request.getHeader(AUTHORIZATION);

        if (token != null && token.startsWith("Bearer "))
            jwtProvider.authenticateJwt(token);

        filterChain.doFilter(request, response);
    }
}
