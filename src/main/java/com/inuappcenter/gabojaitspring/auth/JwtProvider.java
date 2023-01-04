package com.inuappcenter.gabojaitspring.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_AUTHENTICATION_FAIL;
import static java.util.Arrays.stream;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.domain}")
    private String domain;

    private final String tokenPrefix = "Bearer ";
    private final UserDetailsService userDetailsService;

    /**
     * JWT 제작 |
     * Access 토큰과 refresh 토큰을 제작한다.
     */
    public String[] generateJwt(User user) {
        log.info("INITIALIZE | JwtProvider | generateJwt | " + user.getUsername());
        LocalDateTime initTime = LocalDateTime.now();

        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));

        String[] token = new String[2];

        token[0] = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .withIssuer(domain)
                .withClaim("roles",
                        user.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .sign(algorithm);

        token[1] = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 28L * 24 * 60 * 60 * 1000))
                .withIssuer(domain)
                .withClaim("roles",
                        user.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .sign(algorithm);

        log.info("COMPLETE | JwtProvider | generateJwt | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                user.getUsername());
        return token;
    }

    /**
     * JWT 인증
     *
     */
    public void authenticateJwt(String token) {
        log.info("INITIALIZE | JwtProvider | authenticateJwt | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        verifyJwt(token);

        log.info("COMPLETE | JwtProvider | authenticateJwt | " + Duration.between(initTime, LocalDateTime.now()) + " | "
                + token);
    }

    /**
     * JWT 인가
     *
     */
    public String authorizeJwt(String token) {
        log.info("INITIALIZE | JwtProvider | authorizeJwt | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        String username = verifyJwt(token);

        log.info("COMPLETE | JwtProvider | authorizeJwt | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                username);
        return username;
    }

    /**
     * JWT 검증
     *
     */
    public String verifyJwt(String token) {
        token = token.substring(tokenPrefix.length());
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            String username = decodedJWT.getSubject();
            String[] roles = decodedJWT.getClaim("role").asArray(String.class);

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            stream(roles).forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role));
            });

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            return username;
        } catch (Exception e) {
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);
        }
    }
}
