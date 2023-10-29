package com.gabojait.gabojaitspring.auth;

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
            throws ServletException, IOException {

        String accessToken = request.getHeader(AUTHORIZATION);
        String refreshToken = request.getHeader("Refresh-Token");

        if (accessToken != null && !accessToken.isBlank())
            jwtProvider.authenticate(accessToken, Jwt.ACCESS);
        else if (refreshToken != null && !refreshToken.isBlank())
            jwtProvider.authenticate(refreshToken, Jwt.REFRESH);

        filterChain.doFilter(request, response);
    }
}
