package com.inuappcenter.gabojaitspring.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inuappcenter.gabojaitspring.exception.http.UnauthorizedException;
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

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final String secret = "secret";

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
     * JWT 인증 |
     * JWT를 인증한다. JWT 인증 실패시 403(Unauthorized)을 던진다.
     */
    public User verifyJwt(String token) {
        log.info("INITIALIZE | JwtProvider | verifyJwt | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        if (token == null || !token.startsWith(tokenPrefix)) {
            throw new UnauthorizedException();
        }
        token = token.substring(tokenPrefix.length());
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.info("COMPLETE | JwtProvider | verifyJwt | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                username);
        return (User) userDetailsService.loadUserByUsername(username);
    }

    /**
     * JWT로 유저 조회 |
     * JWT로 유저네임을 조회한다. 실패시 403(Unauthorized)를 던진다.
     */
    public String loadUsernameByJwt(String token) {
        log.info("INITIALIZE | JwtProvider | loadUsernameByJwt | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        if (token == null || !token.startsWith(tokenPrefix)) {
            throw new UnauthorizedException("인증에 실패했습니다");
        }

        token = token.substring(tokenPrefix.length());
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();

        log.info("COMPLETE | JwtProvider | loadUsernameByJwt | " + Duration.between(initTime, LocalDateTime.now()) +
                " | " + username);
        return username;
    }
}
