package com.inuappcenter.gabojaitspring.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final String tokenPrefix = "Bearer ";

    /**
     * 내부 필터 |
     * 토큰이 헤더에 존재한다면 토큰을 검증한다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, JWTVerificationException {

        log.info(">>>>>>>>>>>>>>> {} <<<<<<<<<<<<<<<", request.getRequestURI());

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = request.getHeader(AUTHORIZATION);

            if (token != null && token.startsWith(tokenPrefix)) {
                jwtProvider.authenticateJwt(token, null);
            }
        }

        filterChain.doFilter(request, response);

        log.info("<<<<<<<<<<<<<<< {} >>>>>>>>>>>>>>>", request.getRequestURI());
    }
}
