package com.inuappcenter.gabojaitspring.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final String tokenPrefix = "Bearer ";

    /**
     * 내부 필터 작동 |
     * Auth 토큰이 헤더에 존재한다면 토큰 검증을 한다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("INITIALIZE | CustomAuthorizationFilter | doFilterInternal | " + request.getRequestURI());
        LocalDateTime initTime = LocalDateTime.now();

        String token = request.getHeader(AUTHORIZATION);
        if (token != null && token.startsWith(tokenPrefix)) {
            jwtProvider.verifyJwt(token);
        } else {
            filterChain.doFilter(request, response);
        }

        log.info("COMPLETE | CustomAuthorizationFilter | doFilterInternal | " +
                Duration.between(initTime, LocalDateTime.now()) + " | " + request.getRequestURI());
    }
}
