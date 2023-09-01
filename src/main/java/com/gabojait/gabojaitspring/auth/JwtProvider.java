package com.gabojait.gabojaitspring.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gabojait.gabojaitspring.auth.type.Jwt;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.ErrorCode.TOKEN_UNAUTHORIZED;

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

    private final CustomUserDetailsService customuserDetailsService;
    private final String tokenPrefix = "Bearer ";

    /**
     * Guest JWT 생성
     */
    public HttpHeaders createGuestJwt() {
        return generateHeaderWithToken(0L, Set.of(Role.GUEST.name()), false);
    }

    /**
     * User & Admin JWT 생성
     */
    public HttpHeaders createJwt(Long userId, Set<String> roles) {
        return generateHeaderWithToken(userId, roles, true);
    }

    /**
     * Master JWT 생성
     */
    public HttpHeaders createMasterJwt(Long userId, Set<String> roles) {
        return generateHeaderWithToken(userId, roles, false);
    }

    /**
     * 토큰 생성자
     */
    private HttpHeaders generateHeaderWithToken(Long userId, Set<String> roles, boolean isRefreshToken) {
        long time = System.currentTimeMillis();
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        List<String> roleList = new ArrayList<>(roles);

        String accessJwt = JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(new Date(time))
                .withExpiresAt(new Date(time + accessTokenTime))
                .withIssuer(domain)
                .withClaim("roles", roleList)
                .sign(algorithm);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, accessJwt);

        if (isRefreshToken) {
            String refreshJwt = JWT.create()
                    .withSubject(userId.toString())
                    .withIssuedAt(new Date(time))
                    .withExpiresAt(new Date(time + refreshTokenTime))
                    .withIssuer(domain)
                    .withClaim("roles", roleList)
                    .sign(algorithm);

            httpHeaders.add("Refresh-Token", refreshJwt);
        }

        return httpHeaders;
    }

    public void authGuestAccessJwt(String token) {
        authenticate(token, Role.GUEST, Jwt.ACCESS);
    }

    public void authUserAccessJwt(String token) {
        authenticate(token, Role.USER, Jwt.ACCESS);
    }

    public void authUserRefreshJwt(String token) {
        authenticate(token, Role.USER, Jwt.REFRESH);
    }

    public void authAdminAccessJwt(String token) {
        authenticate(token, Role.ADMIN, Jwt.ACCESS);
    }

    public void authAdminRefreshJwt(String token) {
        authenticate(token, Role.ADMIN, Jwt.REFRESH);
    }

    public void authMasterJwt(String token) {
        authenticate(token, Role.MASTER, Jwt.ACCESS);
    }

    /**
     * 식별자 반환 |
     * 403(TOKEN_UNAUTHORIZED)
     */
    public long getId(String token) {
        DecodedJWT decodedJwt = verifyJwt(token);

        try {
            return Long.parseLong(decodedJwt.getSubject());
        } catch (NumberFormatException e) {
            throw new CustomException(e, TOKEN_UNAUTHORIZED);
        }
    }

    /**
     * JWT 인가
     * 401(TOKEN_UNAUTHENTICATED)
     * 403(TOKEN_UNAUTHORIZED)
     */
    private void authenticate(String token, Role role, Jwt jwt) {
        DecodedJWT decodedJwt = verifyJwt(token);
        List<String> roles;
        long id;
        long validTime;

        try {
            roles = decodedJwt.getClaim("roles").asList(String.class);
            id = Long.parseLong(decodedJwt.getSubject());
            validTime = decodedJwt.getExpiresAt().getTime() - decodedJwt.getIssuedAt().getTime();
        } catch (RuntimeException e) {
            throw new CustomException(e, TOKEN_UNAUTHORIZED);
        }

        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
        roles.forEach(r -> authorities.add(new SimpleGrantedAuthority(r)));

        if (!roles.contains(role.name()))
            throw new CustomException(TOKEN_UNAUTHORIZED);

        if (jwt.equals(Jwt.ACCESS) && validTime != accessTokenTime)
            throw new CustomException(TOKEN_UNAUTHORIZED);
        else if (jwt.equals(Jwt.REFRESH) && validTime != refreshTokenTime)
            throw new CustomException(TOKEN_UNAUTHORIZED);

        if (role.equals(Role.USER))
            customuserDetailsService.loadUserById(id);
        else if (role.equals(Role.ADMIN))
            customuserDetailsService.loadAdminById(id);

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(id, "", authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (JWTVerificationException e) {
            throw new CustomException(e, TOKEN_UNAUTHENTICATED);
        }
    }

    /**
     * JWT 검증 |
     * 401(TOKEN_UNAUTHENTICATED)
     */
    private DecodedJWT verifyJwt(String token) {
        if (token == null || token.trim().equals(""))
            throw new CustomException(TOKEN_UNAUTHENTICATED);

        token = token.trim().substring(tokenPrefix.length());
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new CustomException(e, TOKEN_UNAUTHENTICATED);
        }
    }
}
