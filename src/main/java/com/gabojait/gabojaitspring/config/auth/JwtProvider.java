package com.gabojait.gabojaitspring.config.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gabojait.gabojaitspring.api.service.user.UserDetailsService;
import com.gabojait.gabojaitspring.domain.user.Role;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.TOKEN_UNAUTHENTICATED;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${api.jwt.secret}")
    private String secret;

    @Value("${api.jwt.domain}")
    private String domain;

    @Value("${api.jwt.time.access}")
    private long accessTokenTime;

    @Value("${api.jwt.time.refresh}")
    private long refreshTokenTime;

    private final UserDetailsService userDetailsService;
    private static final String tokenPrefix = "Bearer ";

    public HttpHeaders createJwt(Long userId) {
        UserDetails userDetails = userDetailsService.findUserDetails(userId);
        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        long time = System.currentTimeMillis();
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        String accessToken = JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(new Date(time))
                .withExpiresAt(new Date(time + accessTokenTime))
                .withIssuer(domain)
                .withClaim("roles", authorities)
                .sign(algorithm);

        headers.add(HttpHeaders.AUTHORIZATION, accessToken);

        if (authorities.contains(Role.MASTER.name()))
            return headers;

        String refreshToken = JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(new Date(time))
                .withExpiresAt(new Date(time + refreshTokenTime))
                .withIssuer(domain)
                .withClaim("roles",  authorities)
                .sign(algorithm);

        headers.add("Refresh-Token", refreshToken);

        return headers;
    }

    public void authenticate(String token, Jwt jwt) {
        DecodedJWT decodedJWT = decodeJwt(token);

        long validTime = decodedJWT.getExpiresAt().getTime() - decodedJWT.getIssuedAt().getTime();

        switch (jwt) {
            case ACCESS:
                if (validTime != accessTokenTime)
                    throw new CustomException(TOKEN_UNAUTHENTICATED);
                break;
            case REFRESH:
                if (validTime != refreshTokenTime)
                    throw new CustomException(TOKEN_UNAUTHENTICATED);
                break;
            default:
                throw new CustomException(TOKEN_UNAUTHENTICATED);
        }

        long userId = Long.parseLong(decodedJWT.getSubject());
        UserDetails userDetails = userDetailsService.findUserDetails(userId);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(), "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public Long getUserId(String token) {
        return Long.valueOf(decodeJwt(token).getSubject());
    }

    private DecodedJWT decodeJwt(String token) {
        if (token == null || token.equals(""))
            throw new CustomException(TOKEN_UNAUTHENTICATED);

        token = token.substring(tokenPrefix.length());

        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();

        return verifier.verify(token);
    }
}
