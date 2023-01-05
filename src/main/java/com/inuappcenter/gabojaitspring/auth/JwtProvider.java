package com.inuappcenter.gabojaitspring.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final Long accessTokenTime = 30L * 60 * 1000;
    private final Long refreshTokenTime = 28L * 24 * 60 * 1000;

    /**
     * JWT 제작 |
     * Access 토큰과 refresh 토큰을 제작한다.
     */
    public String[] generateJwt(User user) {
        log.info("INITIALIZE | JwtProvider | generateJwt | " + user.getUsername());
        LocalDateTime initTime = LocalDateTime.now();

        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));

        String[] token = new String[2];
        long time = System.currentTimeMillis();

        token[0] = JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date(time))
                .withExpiresAt(new Date(time + accessTokenTime))
                .withIssuer(domain)
                .withClaim("roles",
                        user.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .sign(algorithm);

        token[1] = JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date(time))
                .withExpiresAt(new Date(time + refreshTokenTime))
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
     */
    public List<String> authorizeJwt(String token) {
        log.info("INITIALIZE | JwtProvider | authorizeJwt | " + token);
        LocalDateTime initTime = LocalDateTime.now();

        List<String> verifiedInfo = verifyJwt(token);

        log.info("COMPLETE | JwtProvider | authorizeJwt | " + Duration.between(initTime, LocalDateTime.now()) + " | " +
                verifiedInfo.get(0) + " | " + verifiedInfo.get(1));
        return verifiedInfo;
    }

    /**
     * JWT 검증
     */
    public List<String> verifyJwt(String token) {
        token = token.substring(tokenPrefix.length());
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();
        List<String> verifiedInfo = new ArrayList<>();

        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            stream(roles).forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role));
            });

            Long validTime = decodedJWT.getExpiresAt().getTime() - decodedJWT.getIssuedAt().getTime();
            String type;
            if (validTime.equals(accessTokenTime)) {
                type = JwtType.ACCESS.name();
            } else if (validTime.equals(refreshTokenTime)) {
                type = JwtType.REFRESH.name();
            } else {
                throw new CustomException(TOKEN_AUTHENTICATION_FAIL);
            }

            String username = decodedJWT.getSubject();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            verifiedInfo.add(username);
            verifiedInfo.add(type);
        } catch (Exception e) {
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);
        }

        log.info("PROGRESS | JwtProvider | verifyJwt | " + verifiedInfo.get(0) + " | " + verifiedInfo.get(1));
        return verifiedInfo;
    }
}
