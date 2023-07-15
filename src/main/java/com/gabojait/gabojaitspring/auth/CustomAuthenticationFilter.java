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
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String accessToken = request.getHeader(AUTHORIZATION) == null ?
                "" : request.getHeader(AUTHORIZATION);
        String refreshToken = request.getHeader("Refresh-Token") == null ?
                "" : request.getHeader("Refresh-Token");


        if (uri.matches("\\/api\\/v\\d\\/contact") && method.matches("PATCH"))
            jwtProvider.authGuestAccessJwt(accessToken);
        else if (uri.matches("\\/api\\/v\\d\\/admin\\/[0-9]*$\\/decide") && method.matches("PATCH"))
            jwtProvider.authMasterJwt(accessToken);
        else if (uri.matches("\\/api\\/v\\d\\/admin[\\-\\/a-z0-9]*$"))
            if (uri.matches("\\/api\\/v\\d\\/admin\\/token") && method.matches("POST"))
                jwtProvider.authAdminRefreshJwt(refreshToken);
            else
                jwtProvider.authAdminAccessJwt(accessToken);
        else if (!accessToken.isBlank() || !refreshToken.isBlank())
            if (uri.matches("\\/api\\/v\\d\\/user\\/token") && method.matches("POST"))
                jwtProvider.authUserRefreshJwt(refreshToken);
            else
                jwtProvider.authUserAccessJwt(accessToken);

        filterChain.doFilter(request, response);
    }
}
